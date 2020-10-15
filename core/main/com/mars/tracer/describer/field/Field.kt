@file:Suppress("DuplicatedCode")

package com.mars.tracer.describer.field

import com.mars.tracer.describer.Describer
import com.mars.tracer.describer.DescriberApi
import org.jf.dexlib2.iface.Field
import org.jf.dexlib2.iface.value.EncodedValue

/**
 * 描述类中的方法
 *
 * @param name 名称
 * @param type 变量类型
 * @param initialValue 初始值
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@DescriberApi
internal class Field(
  val name: String? = null,
  val type: String? = null,
  private val initialValue: EncodedValue? = null
) : Describer<Field>() {
  override fun match(sponsor: Field): Boolean {
    // Field 基本信息的判断
    if (name != null && sponsor.name != name ||
      type != null && sponsor.type != type ||
      initialValue != null && sponsor.initialValue != initialValue
    ) return false
    return true
  }

  override fun toString(): String = """
    Field(name=$name, type=$type, initialValue=$initialValue, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}