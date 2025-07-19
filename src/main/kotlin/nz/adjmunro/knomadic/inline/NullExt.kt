package nz.adjmunro.knomadic.inline

import nz.adjmunro.knomadic.KnomadicDsl

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
