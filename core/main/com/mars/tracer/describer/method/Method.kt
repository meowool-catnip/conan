@file:Suppress("DuplicatedCode")

package com.mars.tracer.describer.method

import com.mars.tracer.describer.Describer
import com.mars.tracer.describer.DescriberApi
import com.mars.tracer.describer.common.Extractor
import com.mars.tracer.describer.common.MemberType
import com.mars.tracer.describer.common.MethodParameter
import com.mars.tracer.describer.common.Text
import com.mars.tracer.describer.method.instructions.Instruction
import com.mars.tracer.describer.method.models.Parameters
import com.mars.tracer.tracedClasses
import org.jf.dexlib2.iface.Method

/**
 * 描述类中的方法
 *
 * @param name 名称
 * @param returnType 返回类型
 * @param parameters 参数类型列表
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@DescriberApi
internal open class Method(
  val name: String? = null,
  val returnType: String? = null,
  val parameters: Parameters? = null
) : Describer<Method>() {
  private val instructions = mutableListOf<Instruction>()
  private var extractor: Extractor? = null

  override fun match(sponsor: Method): Boolean {
    // Method 基本信息的判断
    if (
      name != null && sponsor.name != name ||
      returnType != null && sponsor.returnType != returnType ||
      accessFlags != null && !accessFlags!!.match(sponsor.accessFlags) ||
      notAccessFlags != null && !notAccessFlags!!.match(sponsor.accessFlags)
    ) return false

    if (parameters != null) {
      // 对比 '发起追溯的对象' 和描述的参数列表长度
      if (parameters.length != null && sponsor.parameterTypes.size != parameters.length) return false
      // 对比 '发起追溯的对象' 和描述的参数列表内容
      if (parameters.types != null) sponsor.parameterTypes.forEachIndexed { index, charSequence ->
        val describe = parameters.types[index]
        // 如果描述的参数内容中存在 null 则可以代表忽略此下标，但是如果不为 null 且不对等则匹配失败
        if (describe != null && describe != charSequence.toString()) return false
      }
    }

    // Method 内的注解的判断
    annotations.forEach { describe ->
      var matchCount = 0
      sponsor.annotations.forEach { if (describe.match(it)) matchCount++ }
      if (annotations.size != matchCount) return false
    }

    // Method 内的指令的判断
    instructions.forEach { describe ->
      var matchCount = 0
      sponsor.implementation?.instructions?.forEach {
        /** 如果是引用指令且为字符串引用则判断字符串是否匹配 [Text.match] */
        if (describe.match(it)) matchCount++
      }
      // 没有描述指定次数且匹配成功次数小于 1 则直接匹配失败
      if (describe.repeat == null && matchCount < 1) return false
      // 如果有描述次数且匹配到的次数和描述的不一致则匹配失败
      if (describe.repeat != null && describe.repeat != matchCount) return false
    }

    // 提取 Method 内信息到另一个描述者
    if (extractor != null) {
      val extracted = when (val impl = extractor!!.implementation) {
        is MemberType -> sponsor.returnType
        is MethodParameter -> sponsor.parameterTypes[impl.index]
        else -> null
      }?.toString()
      if (extracted != null)
        tracedClasses[extractor!!.result.java.getConstructor().newInstance().originName] = extracted
    }
    return true
  }

  /** 添加描述 */
  operator fun Instruction.unaryPlus() = this@Method.instructions.add(this)
  operator fun Extractor.unaryPlus() = let { extractor = it }

  override fun toString(): String = """
    Method(name=$name, returnType=$returnType, parameters=$parameters, instructions=$instructions, extractor=$extractor, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}