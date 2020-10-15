@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate")

package com.mars.tracer.describer.clazz

import com.mars.tracer._Object
import com.mars.tracer.describer.Describer

/**
 * 描述类的超类路径
 * @param className 超类的路径名称
 */
internal open class Super(val className: String?) : Describer<String?>() {
  override fun match(sponsor: String?) = sponsor == className

  override fun toString(): String = """
    Super(className=$className, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}

/**
 * 描述类的父类为 [Object]
 * @note 尽可能为继承 [Object] 的类添加此描述，这可以提升大量时间！
 */
internal class SuperObject : Super(_Object)

/**
 * 描述类非继承某超类路径
 * @param className 超类的路径名称
 */
internal open class NoSuper(vararg val className: String?) : Describer<String?>() {
  override fun match(sponsor: String?): Boolean {
    var result = true
    className.forEach {
      result = sponsor != it
    }
    return result
  }

  override fun toString(): String = """
    NoSuper(className=$className, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}

/**
 * 描述类的父类并非 [Object]
 * @note 尽可能为非继承 [Object] 的类添加此描述，这可以提升大量时间！
 */
internal class NoSuperObject : NoSuper(_Object)