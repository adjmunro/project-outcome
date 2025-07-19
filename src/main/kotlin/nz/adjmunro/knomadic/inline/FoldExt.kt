package nz.adjmunro.knomadic.inline

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
