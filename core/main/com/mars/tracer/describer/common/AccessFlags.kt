package com.mars.tracer.describer.common

import com.mars.tracer.describer.Describer
import com.mars.tracer.describer.clazz.Class

typealias AccessFlag = org.jf.dexlib2.AccessFlags

/**
 * 描述修饰符
 * @param modifiers 修饰符
 * @tips 为 [Class] 添加此描述可以大幅度增加追溯速度！
 */
internal class AccessFlags(
  private vararg val modifiers: AccessFlag
) : Describer<Int>() {
  override fun match(sponsor: Int): Boolean {
    if (modifiers.isEmpty() && sponsor != 0) return false

    var value: Int? = null
    modifiers.forEach { value = if (value == null) it.value else value!! or it.value }

    // 如果没有此修饰符则匹配失败
    if (sponsor and value!! == 0) return false
    return true
  }

  override fun toString(): String = """
    AccessFlags(modifiers=${modifiers.contentToString()}, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}

/**
 * 描述不存在的修饰符
 * @param modifiers 没有修饰的访问符
 * @tips 为 [Class] 添加此描述可以大幅度增加追溯速度！
 */
internal class NotAccessFlags(
  private vararg val modifiers: AccessFlag
) : Describer<Int>() {
  override fun match(sponsor: Int): Boolean {
    if (modifiers.isEmpty() && sponsor != 0) return false

    var value: Int? = null
    modifiers.forEach { value = if (value == null) it.value else value!! or it.value }

    // 如果存在此修饰符则匹配失败
    if (sponsor and value!! != 0) return false
    return true
  }

  override fun toString(): String = "{ \"modifiers\": $modifiers }"
}