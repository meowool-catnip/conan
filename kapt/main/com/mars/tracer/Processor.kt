package com.mars.tracer

import com.google.auto.service.AutoService
import com.mars.tracer.Processor.Companion.KAPT_GENERATED_NAME
import com.mars.tracer.annotation.LazyTrace
import com.mars.tracer.annotation.Trace
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


/**
 * 用于处理 [Trace] [LazyTrace] 的注解
 *
 * @author 凛
 * @github https://github.com/oh-Rin
 * @date 2020/10/15 - 13:09
 */
@AutoService(Processor::class)
@SupportedOptions(KAPT_GENERATED_NAME)
@IncrementalAnnotationProcessor(ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class Processor : AbstractProcessor() {
  override fun process(
    annotations: MutableSet<out TypeElement>?,
    roundEnv: RoundEnvironment
  ): Boolean {
    // 找出注解了 @Trace 的类
    val traceElements = roundEnv.getAnnotatedTypes<Trace>()
    if (traceElements.isEmpty()) return false
    // 找出注解了 @LazyTrace 的类
    val lazyTraceElements = roundEnv.getAnnotatedTypes<LazyTrace>()

    Builder.build(
      traceSponsors = traceElements,
      lazytraceTypes = lazyTraceElements
    ).writeTo(
      directory = File(processingEnv.options[KAPT_GENERATED_NAME]!!)
    )
    return true
  }

  override fun getSupportedAnnotationTypes() = setOf(
    Trace::class.java.name,
    LazyTrace::class.java.name
  )

  companion object {
    const val KAPT_GENERATED_NAME = "kapt.kotlin.generated"
  }
}