package com.mars.tracer.describer

import com.mars.tracer.describer.common.AccessFlags
import com.mars.tracer.describer.common.Annotation
import com.mars.tracer.describer.common.NotAccessFlags

@DslMarker
internal annotation class DescriberApi

internal abstract class Describer<in T : Any?> {
  open lateinit var originName: String
  protected open var enabledLogger = false

  // TODO 循环匹配 Superclass 可能需要这个
//    lateinit var classes: Set<ClassDef>
  var accessFlags: AccessFlags? = null
  var notAccessFlags: NotAccessFlags? = null
  val annotations = mutableListOf<Annotation>()

  operator fun Annotation.unaryPlus() = this@Describer.annotations.add(this)
  operator fun AccessFlags.unaryPlus() = let { this@Describer.accessFlags = it }
  operator fun NotAccessFlags.unaryPlus() = let { this@Describer.notAccessFlags = it }

  /**
   * 判断 [sponsor] 是否与描述匹配
   * @param sponsor 类追溯发起者，代表着需要追溯起源的对象
   */
  abstract fun match(sponsor: T): Boolean

  protected fun log(message: Any) {
    if (enabledLogger) println("$originName: $message")
  }

  fun inject(originName: String, enabledLogger: Boolean): Describer<T> {
    this.originName = originName
    this.enabledLogger = enabledLogger
    return this
  }

  override fun toString(): String = "Describer(" +
    "accessFlags=$accessFlags, " +
    "notAccessFlags=$notAccessFlags, " +
    "annotations=[${annotations.joinToString(", \n")}]" +
    ")"
}