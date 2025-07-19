package nz.adjmunro.knomadic.inline

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Syntax-sugar for a lambda that folds a receiver type [T] into type [R].
 *
 * *This should primarily be used when chaining.*
 *
 * ```kotlin
 * val result: String = "Hello"
 *
 * // Before:
 * val a = 7
 * val b = if (a % 2 == 0) null else a
 * val c = b?.toString().orEmpty()
 *
 * // After:
 * 7.fold(
 *   predicate = { this % 2 == 0 },
 *   falsy = { null },
 *   truthy = { this }
 * ).toString().orEmpty()
 * ```
 *
 * @param T The type of the receiver.
 * @param R The type of the return value.
 * @param predicate The predicate to evaluate on the receiver.
 * @param falsy The lambda to call if the predicate returns `false`.
 * @param truthy The lambda to call if the predicate returns `true`.
 * @see flatmap
 */
@KnomadicDsl
public inline fun <T, R> T.fold(
    predicate: T.() -> Boolean,
    falsy: T.() -> R,
    truthy: T.() -> R,
): R {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
        callsInPlace(falsy, InvocationKind.AT_MOST_ONCE)
        callsInPlace(truthy, InvocationKind.AT_MOST_ONCE)
    }

    return when (predicate()) {
        true -> truthy()
        false -> falsy()
    }
}

/**
 * Syntax-sugar for a lambda that folds a receiver type [T] into something else of the same type.
 *
 * *This should primarily be used when chaining.*
 *
 * ```kotlin
 * val result: String = "Hello"
 *
 * // Before:
 * val a = 7
 * val b = if (a % 2 == 0) a else a * 2
 * val c = b.toString()
 *
 * // After:
 * 7.flatmap(predicate = { this % 2 == 0 }) { this * 2 }
 *     .toString()
 * ```
 *
 * @param T The type of the receiver.
 * @param predicate The predicate to evaluate on the receiver.
 * @param falsy The lambda to call if the predicate returns `false`.
 * @param truthy The lambda to call if the predicate returns `true`.
 * @see fold
 */
@KnomadicDsl
public inline fun <T> T.flatmap(
    predicate: T.() -> Boolean,
    falsy: T.() -> T = ::caller,
    truthy: T.() -> T = ::caller,
): T = fold(predicate = predicate, falsy = falsy, truthy = truthy)

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
