@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate", "NAME_SHADOWING")

package com.mars.tracer.describer.method.instructions

import com.mars.tracer.describer.DescriberApi
import com.mars.tracer.describer.method.models.Parameters
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.iface.reference.TypeReference
import org.jf.dexlib2.iface.instruction.Instruction as _Instruction

/**
 * 描述方法中的类型引用指令
 *
 * @param opcode 操作码
 * @param repeat 指令在方法中出现的次数
 * @param type 引用类型
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@DescriberApi
internal class TypeReference(
  opcode: Opcode? = null,
  repeat: Int? = null,
  val type: String? = null
) : Instruction(opcode, repeat) {
  override fun match(sponsor: _Instruction): Boolean {
    if (sponsor !is ReferenceInstruction) return false
    val reference = sponsor.reference as? TypeReference ?: return false
    // 如果超类中匹配失败则当前描述也直接匹配失败
    if (!super.match(sponsor)) return false

    if (type != null && reference.type != type) return false
    return true
  }

  override fun toString(): String = """
    TypeReference(type=$type, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}

/**
 * 描述方法中的调用指令
 *
 * @param opcode 操作码 [Opcode.INVOKE**]
 * @param repeat 指令在方法中出现的次数
 * @param clazz 需要调用的目标类
 * @param name 需要调用的目标方法的名称
 * @param returnType 需要调用的目标方法的返回类型
 * @param parameters 需要调用的目标方法的参数类型
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@DescriberApi
internal class MethodReference(
  opcode: Opcode? = null,
  repeat: Int? = null,
  val name: String? = null,
  val returnType: String? = null,
  val parameters: Parameters? = null,
  val clazz: String? = null
) : Instruction(opcode, repeat) {
  override fun match(sponsor: _Instruction): Boolean {
    if (sponsor !is ReferenceInstruction) return false
    val reference = sponsor.reference as? MethodReference ?: return false
    // 如果超类中匹配失败则当前描述也直接匹配失败
    if (!super.match(sponsor)) return false

    // Method 基本信息的判断
    if (name != null && reference.name != name ||
      clazz != null && reference.definingClass != clazz ||
      returnType != null && reference.returnType != returnType
    ) return false

    if (parameters != null) {
      // 对比 '发起追溯的对象' 和描述的参数列表长度
      if (parameters.length != null && reference.parameterTypes.size != parameters.length) return false
      // 对比 '发起追溯的对象' 和描述的参数列表内容
      if (parameters.types != null) reference.parameterTypes.forEachIndexed { index, charSequence ->
        val describe = parameters.types[index]
        // 如果描述的参数内容中存在 null 则可以代表忽略此下标，但是如果不为 null 且不对等则匹配失败
        if (describe != null && describe != charSequence.toString()) return false
      }
    }

    return true
  }

  override fun toString() = """
      {
        "MethodReference": {
          "opcode": "$opcode",
          "repeat": $repeat,
          "name": "$name",
          "returnType": $returnType,
          "parameters": "$parameters",
          "clazz": "$clazz"
        }
      }
    """.trimIndent()
}

/**
 * 描述变量中的调用指令
 *
 * @param opcode 操作码
 * @param repeat 指令在方法中出现的次数
 * @param clazz 需要调用的目标类
 * @param name 需要调用的目标方法的名称
 * @param type 需要调用的目标方法的返回类型
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@DescriberApi
internal class FieldReference(
  opcode: Opcode? = null,
  repeat: Int? = null,
  val name: String? = null,
  val type: String? = null,
  val clazz: String? = null
) : Instruction(opcode, repeat) {
  override fun match(sponsor: _Instruction): Boolean {
    if (sponsor !is ReferenceInstruction) return false
    val reference = sponsor.reference as? FieldReference ?: return false
    // 如果超类中匹配失败则当前描述也直接匹配失败
    if (!super.match(sponsor)) return false

    // Field 基本信息的判断
    if (name != null && reference.name != name ||
      clazz != null && reference.definingClass != clazz ||
      type != null && reference.type != type
    ) return false

    return true
  }

  override fun toString() = """
      {
        "FieldReference": {
          "opcode": "$opcode",
          "repeat": $repeat,
          "name": "$name",
          "type": $type,
          "clazz": "$clazz"
        }
      }
    """.trimIndent()
}