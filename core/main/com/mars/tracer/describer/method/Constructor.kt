@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")

package com.mars.tracer.describer.method

import com.mars.tracer._void
import com.mars.tracer.describer.DescriberApi
import com.mars.tracer.describer.method.models.Parameters

/**
 * 描述类中的方法
 *
 * @param clinit 是否为静态初始化构造函数
 * @param parameters 参数类型列表
 * @warn 传入的参数为 null 则以模糊的形式去推断
 */
@DescriberApi
internal class Constructor(
  parameters: Parameters? = null,
  val clinit: Boolean = parameters?.length == null && parameters?.types == null
) : Method(
  returnType = _void,
  name = if (clinit) "<clinit>" else "<init>",
  parameters = parameters
) {
  override fun toString(): String = """
    Constructor(clinit=$clinit, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}