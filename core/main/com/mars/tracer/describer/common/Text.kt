@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate", "NAME_SHADOWING")

package com.mars.tracer.describer.common

import com.mars.tracer.describer.clazz.Class
import com.mars.tracer.describer.method.Method
import com.mars.tracer.describer.method.instructions.Instruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.iface.instruction.Instruction as _Instruction

/**
 * 描述 类/方法 中的文本
 *
 * @param string 字符串
 * @param repeat 文本在类/方法中出现的次数
 * @param identical true: 目标必须与描述的文本一致才会匹配成功, false: 只需要目标包含描述的文本即可匹配成功
 *
 * @warn 尽可能的在 [Method] 内描述，而不是直接从 [Class] 描述整个类中的文本，代价会很昂贵！
 */
internal class Text(
  val string: String,
  repeat: Int? = null,
  private val identical: Boolean = true
) : Instruction(repeat = repeat) {
  override fun match(sponsor: _Instruction): Boolean {
    val sponsor = sponsor as? ReferenceInstruction ?: return false
    val reference = sponsor.reference as? StringReference ?: return false
    return if (identical) reference.string == string else reference.string.contains(string)
  }

  override fun toString(): String = """
    Text(string='$string', identical=$identical, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}