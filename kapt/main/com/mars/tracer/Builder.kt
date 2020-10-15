@file:Suppress("SpellCheckingInspection", "DEPRECATION", "NestedLambdaShadowedImplicitParameter")

package com.mars.tracer

import com.hendraanggrian.kotlinpoet.TypeSpecBuilder
import com.hendraanggrian.kotlinpoet.buildFileSpec
import com.hendraanggrian.kotlinpoet.collections.FunSpecListScope
import com.hendraanggrian.kotlinpoet.collections.ParameterSpecListScope
import com.hendraanggrian.kotlinpoet.lambdaBy
import com.mars.tracer.annotation.Trace
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.text.SimpleDateFormat
import java.util.*
import javax.lang.model.element.TypeElement

/**
 * 生成 Tracer 源码的实现类
 *
 * @author 凛
 * @github https://github.com/oh-Rin
 * @date 2020/10/15 - 10:48
 */
object Builder {
  private val CLASS = ClassName("com.mars.tracer.describer.clazz", "Class")
  private val CLASS_DEF = ClassName("org.jf.dexlib2.iface", "ClassDef")
  private val DEX_FILE = ClassName("org.jf.dexlib2.iface", "DexFile")
  private val FIELD = ClassName("org.jf.dexlib2.iface", "Field")
  private val METHOD = ClassName("org.jf.dexlib2.iface", "Method")
  private val ANNOTATION = ClassName("org.jf.dexlib2.iface", "Annotation")


  /**
   *
   *
   * @param traceSponsors 发起追溯的描述类
   * @param lazytraceTypes 需要追溯的描述类，
   * 但并不是由他们发起并执行追溯，
   * 而是由注解了 [Trace] 的描述类来填充
   * ```
   * ExtractTo<AnnotatedDescriber>(...)
   * ```
   */
  fun build(
    traceSponsors: List<TypeElement>,
    lazytraceTypes: List<TypeElement>
  ) = buildFileSpec(packageName = "com.mars.tracer", fileName = "Tracer") {
    annotations {
      Suppress::class {
        useSiteTarget = AnnotationSpec.UseSiteTarget.FILE
        addMember("\"MemberVisibilityCanBePrivate\", \"SpellCheckingInspection\"")
      }
    }
    types.addClass("Tracer") {
      kdoc {
        appendLine("实现快速追溯预期类")
        appendLine()
        appendLine("@author 凛")
        appendLine("@github https://github.com/oh-Rin")
        appendLine("@date ${SimpleDateFormat("yyyy/MM/dd - HH:mm").format(Date())}")
      }
      addProperties(traceSponsors, lazytraceTypes)
      functions {
        addStartDexFunc()
        addStartClassesFunc()
        addMatchFuncs(traceSponsors)
      }
    }
  }

  /** 创建所有 '描述者' 的实例以及起源映射的变量 */
  private fun TypeSpecBuilder.addProperties(
    traceSponsors: List<TypeElement>,
    lazytraceTypes: List<TypeElement>
  ) = properties {
    // 我们需要一个记录了无论是注解了 @Trace, @LazyTrace 的变量
    val allTypes = traceSponsors + lazytraceTypes

    // 实例化他们到变量中
    allTypes.forEach {
      val type = it.asClassName()
      // 添加描述者的变量实例
      add(it.toPropertyName(), type) {
        addModifiers(KModifier.PRIVATE)
        initializer("%T()", type)
      }
    }

    // 然后为他们创建一个变量来映射这些 '描述者' 的起源名
    "traceOriginNames"(LIST.parameterizedBy(STRING)) {
      kdoc {
        appendLine("记录所有描述者需要追溯到的起源类")
        appendLine("@key 追溯的目标名")
      }
      initializer {
        callFunction(
          target = "listOf",
          parameters = allTypes.map { "${it.toPropertyName()}.originName" }.toTypedArray()
        )
      }
    }
  }


  /**
   * 创建 start 函数
   * ```
   * fun start(
   *   dex: DexFile,
   *   onEachStart: ...,
   *   onEachEnd: ...,
   * ) { ... }
   * ```
   */
  private fun FunSpecListScope.addStartDexFunc() = "start" {
    kdoc.appendLine("开始追溯 App 可能存在的混淆类")
    parameters {
      "dex"(DEX_FILE) {
        kdoc.append("可能存在混淆类的 App Dex")
      }
      addEachCallbacks()
    }
    appendLine("return start(dex.classes, onEachStart, onEachEnd)")
  }

  /**
   * 创建 start 函数
   * ```
   * fun start(
   *   classes: Collection<ClassDef>,
   *   onEachStart: ...,
   *   onEachEnd: ...,
   * ) { ... }
   * ```
   */
  private fun FunSpecListScope.addStartClassesFunc() = "start" {
    kdoc.appendLine("开始追溯 App 可能存在的混淆类")
    parameters {
      "classes"(COLLECTION.parameterizedBy(CLASS_DEF)) {
        kdoc.append("需要遍历的 Dex 类集")
      }
      addEachCallbacks()
    }
    controlFlow("return classes.sortedBy") {
      addComment("首先我们将类名中没有·'/'·的排到最前面，因为这是·Root-Package·中的类，优先追溯以加快速度")
      appendLine("it.contains(\"/\")")
    }
    controlFlow(".forEachIndexed { index, clazz ->") {
      appendLine("onEachStart?.invoke(index, clazz)")
      // 遍历 classes
      appendLine("clazz.matchAll()")
      // 遍历 methods
      appendLine("clazz.methods.matchAllMethods()")
      // 遍历 fields
      appendLine("clazz.fields.matchAllFields()")
      // 遍历 annotations
      appendLine("clazz.annotations.matchAllAnnotations()")
      // 检查所有匹配结果
      appendLine("clazz.checkAllMatch()")
      appendLine("onEachEnd?.invoke(index, clazz)")
    }
  }

  /**
   * 创建多个匹配函数
   * ```
   * matchAll
   * matchAllMethods
   * matchAllFields
   * matchAllAnnotations
   * checkAllMatch
   * ```
   */
  private fun FunSpecListScope.addMatchFuncs(traceSponsors: List<TypeElement>) {
    // 创建 ClassDef.matchAll() { ... }
    "matchAll" {
      receiver = CLASS_DEF
      addModifiers(KModifier.PRIVATE)
      traceSponsors.forEach {
        appendLine("${it.toPropertyName()}.matchClass(this)")
      }
    }

    // 创建 Iterable<*>.matchAll*() = forEach { ... }
    arrayOf(
      "Methods" to METHOD,
      "Fields" to FIELD,
      "Annotations" to ANNOTATION
    ).map {
      val name = it.first
      val typeName = it.second
      "matchAll$name" {
        receiver = ITERABLE.parameterizedBy(typeName)
        addModifiers(KModifier.PRIVATE)
        controlFlow("return forEach") {
          traceSponsors.forEach {
            appendLine("${it.toPropertyName()}.match$name(it)")
          }
        }
      }
    }

    // 创建 ClassDef.checkAllMatch() { ... }
    "checkAllMatch" {
      receiver = CLASS_DEF
      addModifiers(KModifier.PRIVATE)
      traceSponsors.forEach {
        appendLine("${it.toPropertyName()}.checkAllMatch(this)")
      }
    }
  }

  /**
   * 添加两个参数作为 [List.forEach] 遍历时的回调
   * ```
   * onEachStart: ((index: Int, clazz: ClassDef) -> Unit)? = null
   * onEachEnd: ((index: Int, clazz: ClassDef) -> Unit)? = null
   * ```
   */
  private fun ParameterSpecListScope.addEachCallbacks() {
    fun addParameter(argName: String, type: String) = add(
      name = argName,
      type = UNIT.lambdaBy(
        ParameterSpec("index", INT),
        ParameterSpec("clazz", CLASS_DEF)
      ).copy(nullable = true)
    ) {
      kdoc.append("Dex 的类每次遍历${type}时的回调")
      defaultValue("null")
    }

    addParameter("onEachStart", "开始")
    addParameter("onEachEnd", "结束")
  }


  /** 将 `com.foo.Bar` 转为 `bar` 以作为属性名 */
  private fun TypeElement.toPropertyName() = simpleName.toString().decapitalize()
}