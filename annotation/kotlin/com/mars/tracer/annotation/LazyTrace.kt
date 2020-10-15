package com.mars.tracer.annotation

/**
 * 代表这个 '描述者' 不会被追溯源头
 * 而是让其他注解了 [Trace] 的 '描述者' 来填充源数据
 *
 * ```
 * class Other {
 *   init {
 *     ExtractTo<AnnotatedDescriber>(...)
 *   }
 * }
 * ```
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LazyTrace