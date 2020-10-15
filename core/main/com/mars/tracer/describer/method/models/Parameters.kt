package com.mars.tracer.describer.method.models

/**
 * 描述类中的方法
 *
 * @param length 可设置具体有多少个类型
 * @param types 类型
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@Suppress("ArrayInDataClass")
internal data class Parameters(
  val types: Array<out String?>? = null,
  val length: Int? = types?.size
)


/** 参数类型 [Parameters] */
internal fun parametersOf(vararg types: String?, assignLength: Boolean = true) = Parameters(
  types = types,
  length = if (assignLength) types.size else null
)

/**
 * 空参数
 * @warn 注意：'emptyParameters' 与 null 'parameters' 是不一样的
 */
internal fun emptyParameters() = Parameters(types = emptyArray(), length = 0)