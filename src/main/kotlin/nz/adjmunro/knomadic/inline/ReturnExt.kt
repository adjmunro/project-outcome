@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.inline

import kotlinx.coroutines.flow.FlowCollector
import nz.adjmunro.knomadic.KnomadicDsl

/**
 * Syntax-sugar for a lambda that returns `it`.
 *
 * ```kotlin
 * fun <T> T.map(transform: (T) -> T): T
 *
 * // Before:
 * map(transform = { it })
 *
 * // After:
 * map(transform = ::it)
 * ```
 *
 * @return The first argument passed to the lambda.
 */
@KnomadicDsl
public inline fun <T> itself(value: T): T = value

/**
 * Syntax-sugar for a lambda that returns `this`.
 *
 * ```kotlin
 * fun <T> T.map(transform: T.() -> T): T
 *
 * // Before:
 * map(transform = { this })
 *
 * // After:
 * map(transform = ::caller)
 * ```
 *
 * @return The receiver of the lambda.
 */
@KnomadicDsl
public inline fun <T> T.caller(ignore: Any? = null): T = this@caller

/**
 * Syntax-sugar for a lambda that throws `it` (provided `it` is a [Throwable]).
 *
 * ```kotlin
 * fun <T> T.map(transform: (T) -> T): T
 *
 * // Before:
 * map(transform = { throw it })
 *
 * // After:
 * map(transform = ::rethrow)
 * ```
 *
 * @throws [Throwable] passed as the first argument to the lambda.
 */
@KnomadicDsl
public inline fun rethrow(throwable: Throwable): Nothing = throw throwable

/**
 * Syntax-sugar for a lambda that throws `it` (provided `it` is a [Throwable])
 * within a [FlowCollector] context.
 *
 * @throws [Throwable] passed as the first argument to the lambda.
 */
@KnomadicDsl
public inline fun <T: Any> rethrow(collector: FlowCollector<T>, throwable: Throwable): Nothing =
    throw throwable

/**
 * Syntax-sugar for a lambda that returns `null`.
 *
 * ```kotlin
 * fun <T> T.map(transform: (T) -> T?): T?
 *
 * // Before:
 * map(transform = { null })
 *
 * // After:
 * map(transform = ::nulls)
 * ```
 *
 * @return `null`.
 */
@KnomadicDsl
public inline fun nulls(ignore: Any? = null): Nothing? = null

/**
 * Syntax-sugar for a lambda that returns [Unit].
 *
 * ```kotlin
 * fun <T> T.map(transform: (T) -> Unit): Unit
 *
 * // Before:
 * map(transform = { /* do nothing */ })
 *
 * // After:
 * map(transform = ::unit)
 * ```
 *
 * @return [Unit].
 */
@KnomadicDsl
public inline fun unit(ignore: Any? = null): Unit = Unit

/**
 * Syntax-sugar for a lambda that returns the [String] of `it`.
 *
 * ```kotlin
 * fun <T> T.map(transform: (T) -> String): String
 *
 * // Before:
 * map(transform = { "$it" })
 *
 * // After:
 * map(transform = ::stringItself)
 * ```
 *
 * @return String of the first argument passed to the lambda.
 */
@KnomadicDsl
public inline fun <T> stringItself(value: T): String = value.toString()

/**
 * Syntax-sugar for a lambda that returns the [String] of `this`.
 *
 * ```kotlin
 * fun <T> T.map(transform: T.() -> String): String
 *
 * // Before:
 * map(transform = { "$this" })
 *
 * // After:
 * map(transform = ::stringCaller)
 * ```
 *
 * @return String of the lambda's receiver.
 */
@KnomadicDsl
public inline fun <T> T.stringCaller(ignore: Any? = null): String = this@stringCaller.toString()

/**
 * Syntax-sugar for a lambda that returns an empty [String].
 *
 * ```kotlin
 * fun <T> T.map(transform: (T) -> String): String
 *
 * // Before:
 * map(transform = { "" })
 *
 * // After:
 * map(transform = ::emptyString)
 * ```
 *
 * @return An empty [String].
 */
@KnomadicDsl
public inline fun emptyString(ignore: Any? = null): String = ""
