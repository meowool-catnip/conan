@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate")

package com.mars.tracer.describer.method.instructions

import com.mars.tracer.describer.Describer
import com.mars.tracer.describer.DescriberApi
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.Instruction as _Instruction

/**
 * 描述方法中的指令
 *
 * @param opcode 操作码
 * @param repeat 指令在方法中出现的次数
 */
@DescriberApi
internal open class Instruction(val opcode: Opcode? = null, val repeat: Int? = null) :
  Describer<_Instruction>() {
  override fun match(sponsor: _Instruction): Boolean {
    if (opcode != null && sponsor.opcode != opcode) return false
    return true
  }

  override fun toString(): String = """
    Instruction(opcode=$opcode, repeat=$repeat, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}