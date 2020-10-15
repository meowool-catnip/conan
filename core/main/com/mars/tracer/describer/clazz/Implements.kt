@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate")

package com.mars.tracer.describer.clazz

import com.mars.tracer.describer.Describer

/**
 * 描述类的实现接口
 * @param containInterfaces 包含实现的接口（不确定接口的 index 是否会变化时可以描述它）
 * @param interfaces 实现的接口（其中如果传入了 null 则以模糊的形式去推断）
 * @param minLength 至少存在的接口数量（不确定接口数量是否会变化的时候可以描述它）
 * @param length 接口数量
 * @warn 注意描述的接口顺序
 */
internal open class Implements(
  val containInterfaces: Array<String?>? = null,
  val interfaces: Array<String?>? = null,
  val minLength: Int? = null,
  val length: Int? = interfaces?.size
) : Describer<Array<String>>() {
  override fun match(sponsor: Array<String>): Boolean {
    if (minLength != null && sponsor.size < minLength) return false
    if (length != null && sponsor.size != length) return false
    if (interfaces != null) sponsor.forEachIndexed { index, string ->
      val describe = interfaces[index]
      // 如果描述的数组内容中存在 null 则可以代表忽略此下标，但是如果不为 null 且不对等则匹配失败
      if (describe != null && describe != string) return false
    }
    if (containInterfaces != null && !sponsor.toList()
        .containsAll(containInterfaces.toList())
    ) return false
    return true
  }

  override fun toString(): String = """
    Implements(
      containInterfaces=${containInterfaces?.contentToString()}, 
      interfaces=${interfaces?.contentToString()},
      minLength=$minLength, 
      length=$length, 
      parentInfo={
        ${super.toString()}
      }
    )
  """.trimIndent()
}

/** 描述类不存在任何接口 */
internal class NoImplements : Implements(length = 0) {
  override fun toString(): String = """
    NoImplements(
      containInterfaces=${containInterfaces?.contentToString()}, 
      interfaces=${interfaces?.contentToString()},
      minLength=$minLength, 
      length=$length, 
      parentInfo={
        ${super.toString()}
      }
    )
  """.trimIndent()
}