@file:Suppress(
  "PARAMETER_NAME_CHANGED_ON_OVERRIDE", "MemberVisibilityCanBePrivate", "DuplicatedCode",
  "KDocUnresolvedReference", "SpellCheckingInspection"
)

package com.mars.tracer.describer.clazz

import com.mars.tracer.describer.Describer
import com.mars.tracer.describer.DescriberApi
import com.mars.tracer.describer.common.*
import com.mars.tracer.describer.common.Annotation
import com.mars.tracer.describer.field.Field
import com.mars.tracer.describer.method.Method
import com.mars.tracer.tracedClasses
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Annotation as _Annotation
import org.jf.dexlib2.iface.Field as _Field
import org.jf.dexlib2.iface.Method as _Method

/**
 * 描述类（高性能模式）
 * @param originName 追溯的类起源
 * @param superObject 指定父类为 Object 的类（优先级比 [noSuperObject] 高）
 * @param normalAccess 排除非正常类的修饰符
 * @param noSuperObject 排除父类为 Object 的类
 * @param noImplements 排除拥有接口实现的类
 * @param enabledLogger 开启日志输出调试
 * @param alwaysTrace 无论如何都必须进行追溯
 * 如果为 false 则会先判断 [originName] 是否没有混淆，没有混淆则不会执行匹配
 * ⚠️ 如果使用了 [Extractor] 对准此类，则必须开启 [alwaysTrace]
 */
@DescriberApi
internal abstract class HighSpeedClass(
  originName: String,
  superObject: Boolean = false,
  normalAccess: Boolean = true,
  noSuperObject: Boolean = true,
  noImplements: Boolean = true,
  enabledLogger: Boolean = false,
  alwaysTrace: Boolean = false
) : Class(originName, enabledLogger, alwaysTrace) {
  init {
    if (normalAccess) +NotAccessFlags(
      AccessFlag.STATIC,
      AccessFlag.ABSTRACT,
      AccessFlag.ENUM,
      AccessFlag.INTERFACE
    )
    if (superObject) +SuperObject() else if (noSuperObject) +NoSuperObject()
    if (noImplements) +NoImplements()
  }
}

/**
 * 描述类
 * @param originName 追溯的类起源
 * @param enabledLogger 开启日志输出调试
 * @param alwaysTrace 无论如何都必须进行追溯
 * 如果为 false 则会先判断 [originName] 是否没有混淆，没有混淆则不会执行匹配
 * ⚠️ 如果使用了 [Extractor] 对准此类，则必须开启 [alwaysTrace]
 */
@DescriberApi
internal abstract class Class(
  override var originName: String,
  override var enabledLogger: Boolean = false,
  private val alwaysTrace: Boolean = false
) : Describer<ClassDef>() {

  /**
   * 一些基础描述
   * @warn 可以的话尽可能加上以下描述，这些是高性能的优先描述，如果匹配失败则不会运行其他耗时描述
   *       这可能会让追溯速度上升十倍！
   */
  private var superclass: Super? = null
  private var noSuperclass: NoSuper? = null
  private var implements: Implements? = null

  /**
   * 较为耗时的描述
   * @see Field.unaryPlus 描述变量
   * @see Method.unaryPlus 描述方法
   */
  val fields = mutableListOf<Field>()
  val methods = mutableListOf<Method>()

  /** 如果类没有被模糊，则直接跳过所有匹配调用 */
  private var skipMatch = false

  /** 最优先的匹配，只有这个匹配成功才会执行子描述的匹配 */
  private var classMatched = true

  /**
   * 记录匹配成功的子描述 (ID)
   * @see Describer.toString
   */
  private val matchedAnnotations = mutableListOf<Annotation>()
  private val matchedFields = mutableListOf<Field>()
  private val matchedMethods = mutableListOf<Method>()

  override fun match(sponsor: ClassDef): Boolean {
    TODO("Not implemented. See method: matchClass, matchAnnotations, matchFields, matchMethods, checkAllMatch.")
  }

  /** 匹配类描述 */
  fun matchClass(sponsor: ClassDef) {
    // 重置上一次循环 class 时的匹配状态
    matchedAnnotations.clear()
    matchedFields.clear()
    matchedMethods.clear()
    classMatched = true

    // 首先判断当前类是否没有被混淆，没有被混淆则直接跳过，避免不必要的追溯所耗费的时间
    if (tracedClasses[originName] == null) {
      skipMatch = false
      // 判断源路径是否和当前循环到的类路径一致，如果存在则记录（这代表着类没有被混淆）
      if (sponsor.type == "L" + originName.replace(".", "/") + ";") {
        // 记录后将不会进行追溯，这是对 *App 可能在某个版本类不再混淆后的措施
        tracedClasses[originName] = sponsor.type
        skipMatch = true
      }
    } else skipMatch = true

    if (skipMatch) return

    // FIXME 支持 superclass 循环匹配直到 object（前提需解决耗时过长的问题）
    if (superclass != null &&
      !superclass!!.inject(originName, enabledLogger).match(sponsor.superclass)
    ) classMatched = false
    if (noSuperclass != null &&
      !noSuperclass!!.inject(originName, enabledLogger).match(sponsor.superclass)
    ) classMatched = false
    if (implements != null &&
      !implements!!.inject(originName, enabledLogger).match(sponsor.interfaces.toTypedArray())
    ) classMatched = false
    if (accessFlags != null &&
      !accessFlags!!.inject(originName, enabledLogger).match(sponsor.accessFlags)
    ) classMatched = false
    if (notAccessFlags != null &&
      !notAccessFlags!!.inject(originName, enabledLogger).match(sponsor.accessFlags)
    ) classMatched = false
  }

  fun matchAnnotations(sponsor: _Annotation) {
    if (!classMatched || skipMatch) return
    annotations.forEach {
      // 描述者与发起追溯的注解匹配则记录
      if (it.inject(originName, enabledLogger).match(sponsor)) matchedAnnotations.add(it)
    }
  }

  fun matchFields(sponsor: _Field) {
    if (!classMatched || skipMatch) return
    fields.forEach {
      // 描述者与发起追溯的变量匹配则记录
      if (it.inject(originName, enabledLogger).match(sponsor)) matchedFields.add(it)
    }
  }

  fun matchMethods(sponsor: _Method) {
    if (!classMatched || skipMatch) return
    methods.forEach {
      // 描述者与发起追溯的方法匹配则记录
      if (it.inject(originName, enabledLogger).match(sponsor)) matchedMethods.add(it)
    }
  }

  fun checkAllMatch(sponsor: ClassDef) {
    if (!classMatched || skipMatch) return
    if (matchedAnnotations.size != annotations.size) return
    if (matchedMethods.size != methods.size) return
    if (matchedFields.size != fields.size) return
    if (!matchedAnnotations.containsAll(annotations)) return
    if (!matchedMethods.containsAll(methods)) return
    if (!matchedFields.containsAll(fields)) return

    // 追溯成功，记录发起追溯的类名称
    tracedClasses[originName] = sponsor.type
  }

  /** 添加描述 */
  operator fun Text.unaryPlus() = this@Class.methods.add(Method().apply { +this@unaryPlus })
  operator fun Method.unaryPlus() = this@Class.methods.add(this)
  operator fun Field.unaryPlus() = this@Class.fields.add(this)
  operator fun Super.unaryPlus() = let { superclass = it }
  operator fun NoSuper.unaryPlus() = let { noSuperclass = it }
  operator fun Implements.unaryPlus() = let { implements = it }
}