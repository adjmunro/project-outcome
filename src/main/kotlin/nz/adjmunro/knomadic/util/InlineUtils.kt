@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.util

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

/**
 * Syntax-sugar for `null`-case of [nullfold] that throws a [NullPointerException] by default.
 *
 * > *This mainly exists for the scenario where you would use a block and/or still want to
 * > continue function chaining.*
 *
 * ```kotlin
 * // Before -- awkward run block / breaks function chaining
 * val len = result?.length ?: run {
 *     state.update { it.copy(error = "Result was null") }
 *     return@someFunction
 * }
 * println(len.toString())
 *
 * // Before -- awkward brackets to continue function chain
 * val len = (result?.length ?: run {
 *     state.update { it.copy(error = "Result was null") }
 *     return@run -1
 * }).let { println(it.toString()) }
 *
 * // After
 * result?.length.fallback {
 *     state.update { it.copy(error = "Result was null") }
 *     return@someFunction // or return@fallback -1
 * }.let { println(it.toString()) }
 */
@KnomadicDsl
public inline infix fun <T : Any> T?.fallback(none: (NullPointerException) -> T = ::rethrow): T {
    return nullfold(none = none, some = ::itself)
}

/** Syntax-sugar equivalent to a [with] block that only executes if the [receiver] is not `null`. */
@KnomadicDsl @JvmName("withIfExists")
public inline fun <T, R> exists(receiver: T?, block: (T & Any).() -> R): R? {
    return receiver.nullfold(none = ::nulls, some = block)
}

/** Syntax-sugar equivalent to a [run] block that only executes if the receiver is not `null`. */
@KnomadicDsl @JvmName("letIfExists")
public inline infix fun <T, R> T?.exists(block: (T & Any).() -> R): R? {
    return nullfold(none = ::nulls, some = block)
}

/**
 * Syntax-sugar for a lambda folds a nullable receiver type into type [Out].
 *
 * *Looks dumb, but weirdly useful in the right context.*
 *
 * ```kotlin
 * val result: String? = null
 *
 * // Before:
 * result?.length ?: 0
 *
 * // Before:
 * when (result) {
 *    null -> 0
 *    else -> result.length
 * }
 *
 * // After:
 * result.nullfold(none = { 0 }, some = { it.length })
 * ```
 *
 * @param In The type of the receiver.
 * @param Out The type of the return value.
 * @param none The lambda to call if the receiver is `null`.
 * @param some The lambda to call if the receiver is not `null`.
 * @return The result of the lambda passed to [some] or [none].
 */
@KnomadicDsl
public inline fun <In, Out> In.nullfold(
    none: (NullPointerException) -> Out,
    some: (In & Any) -> Out,
): Out {
    contract {
        callsInPlace(some, InvocationKind.AT_MOST_ONCE)
        callsInPlace(none, InvocationKind.AT_MOST_ONCE)
    }

    return when (this@nullfold) {
        null -> none(NullPointerException("Nullfold source was null."))
        else -> some(this@nullfold)
    }
}

/**
 * Syntax-sugar for a lambda folds a potentially [Throwable] receiver type into type [Out].
 *
 * *This function does not catch exceptions.*
 *
 * ```kotlin
 * val result: Throwable = IllegalStateException()
 *
 * // Before:
 * (result as? Throwable)?.let { "$it" } ?: "The world is good!"
 *
 * // After:
 * result.throwfold(throws = { "$it" }, pass = { "The world is good!" })
 * ```
 *
 * @param In The type of the receiver.
 * @param Out The type of the return value.
 * @param throws The lambda to call if the receiver is a [Throwable].
 * @param pass The lambda to call if the receiver is not a [Throwable].
 * @return The result of the lambda passed to [throws] or [pass].
 */
@KnomadicDsl
public inline fun <In, Out> In.throwfold(
    throws: (Throwable) -> Out,
    pass: (In) -> Out,
): Out {
    contract {
        callsInPlace(pass, InvocationKind.AT_MOST_ONCE)
        callsInPlace(throws, InvocationKind.AT_MOST_ONCE)
    }

    return when (this@throwfold) {
        is Throwable -> throws(this@throwfold)
        else -> pass(this@throwfold)
    }
}
