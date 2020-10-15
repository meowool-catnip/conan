@file:Suppress("FunctionName")

package com.mars.tracer.describer.common

import com.mars.tracer.describer.clazz.Class
import kotlin.reflect.KClass

internal interface ExtractImplementation


/**
 * 从描述结果中提取信息
 * @param implementation 具体实施
 * @param result 需要将信息提取到的 [Class]
 */
internal data class Extractor(
  val implementation: ExtractImplementation,
  val result: KClass<out Class>
)


/**
 * 从描述结果中提取信息
 * @param implementation 具体实施
 * @see Result 需要将信息提取到的 [Class]
 */
internal inline fun <reified Result : Class> ExtractTo(implementation: ExtractImplementation) =
  Extractor(implementation, Result::class)


/**
 * 提取 Method 参数中的类型
 * @param index 需要提取参数列表中的类型的下标
 */
internal data class MethodParameter(val index: Int) : ExtractImplementation


/** 提取 Method / Field 的返回类型 */
internal object MemberType : ExtractImplementation {
  override fun toString(): String = "MemberType"
}