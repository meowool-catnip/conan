package com.mars.tracer

/**
 * 记录没有混淆与混淆后的类名
 *
 * @author 凛
 * @github https://github.com/oh-Rin
 * @date 2020/10/15 - 06:17
 * @see Source
 * @see Trace
 */
val tracedClasses: MutableMap<Source, Trace> by lazy { hashMapOf() }

/**
 * 起源类名
 * 既没被混淆时，人类可读的类名
 */
private typealias Source = String

/**
 * 发起追溯的类名
 * 既已经被混淆后，需要找到对应的 [Source] 的发起者
 */
private typealias Trace = String