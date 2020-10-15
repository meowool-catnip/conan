package com.mars.tracer

/**
 * 封装一些该死的模板代码
 *
 * @author 凛
 * @github https://github.com/oh-Rin
 * @date 2020/10/15 - 10:35
 */
import com.hendraanggrian.kotlinpoet.CodeBlockBuilder
import com.hendraanggrian.kotlinpoet.FunSpecBuilder
import com.hendraanggrian.kotlinpoet.collections.FunSpecListScope
import com.squareup.kotlinpoet.KModifier
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement


/** 从 [RoundEnvironment] 获取注解了 [A] 的类型元素 */
inline fun <reified A : Annotation> RoundEnvironment.getAnnotatedTypes(): List<TypeElement> =
  getElementsAnnotatedWith(A::class.java).filterIsInstance<TypeElement>()

/** 创建一个可暂停的函数 */
inline fun FunSpecListScope.suspendFunction(
  name: String,
  builderAction: FunSpecBuilder.() -> Unit
) = name {
  addModifiers(KModifier.SUSPEND)
  builderAction()
}

/** 创建控制流块 */
inline fun FunSpecBuilder.controlFlow(
  flow: String,
  vararg args: Any,
  buildFlowAction: FunSpecBuilder.() -> Unit
) {
  beginFlow(flow, args)
  buildFlowAction()
  endFlow()
}

/**
 * 创建调用方法
 * @param target 调用的目标方法名称
 */
fun FunSpecBuilder.callFunction(
  target: String,
  vararg parameters: String
) = append {
  appendLine("$target(")
  indent()
  parameters.forEach {
    appendLine("$it, ")
  }
  unindent()
  appendLine(")")
}

/**
 * 创建调用方法
 * @param target 调用的目标方法名称
 */
fun CodeBlockBuilder.callFunction(
  target: String,
  vararg parameters: String
) = append {
  appendLine("$target(")
  indent()
  parameters.forEach {
    appendLine("$it, ")
  }
  unindent()
  appendLine(")")
}