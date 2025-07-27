package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Failure
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.inline.itself
import nz.adjmunro.knomadic.outcome.Success
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

/**
 * `Fold` the [caller][Outcome] into some [Output].
 *
 * - Unlike [map][Outcome.map], `fold` places no restrictions on [Output] type.
 * - If [Output] is [Outcome], `fold` can be used to `flatMap` both outcomes.
 * - *Use [collapse][Outcome.collapse] to assume the nearest common `Ancestor` as the [Output] type.*
 *
 * @receiver The [Outcome] to collapse.
 * @return The collapsed value of type [Output].
 *
 * @param Ok The type of the [Success] value.
 * @param Error The type of the [Failure] error.
 *
 * @param success The lambda to transform the [Success] value into [Output].
 * @param failure The lambda to transform the [Failure] error into [Output].
 *
 * @see Outcome.rfold
 * @see Outcome.collapse
 */
@KnomadicDsl
public inline fun <Ok, Error, Output> Outcome<Ok, Error>.fold(
    success: (Ok) -> Output,
    failure: (Error) -> Output,
): Output where Ok : Any, Error : Any, Output : Any? {
    contract {
        callsInPlace(success, AT_MOST_ONCE)
        callsInPlace(failure, AT_MOST_ONCE)
    }

    return when (this@fold) {
        is Outcome.Success<Ok> -> success(value)
        is Outcome.Failure<Error> -> failure(error)
    }
}

/**
 * *Reverse [fold][Outcome.fold] is syntax-sugar that swaps the lambda argument order.*
 *
 * ```kotlin
 * val outcome: Outcome<String, Throwable> = ...
 *
 * throw outcome.rfold(failure = ::itself) { // it: Ok ->
 *    IllegalStateException("Expected an error, got: $it")
 * }
 * ```
 *
 * @see Outcome.fold
 * @see Outcome.collapse
 */
@KnomadicDsl
public inline fun <Ok, Error, Output> Outcome<Ok, Error>.rfold(
    failure: (Error) -> Output,
    success: (Ok) -> Output,
): Output where Ok : Any, Error : Any, Output : Any? {
    return fold(success = success, failure = failure)
}

/**
 * Collapse the receiver [Outcome] into either [value][Success.value] or
 * [error][Failure.error], using the nearest common [Ancestor] as the type.
 *
 * *Use [Outcome.fold] with `success` and `failure` lambda arguments
 * to directly map each state to a specific `Output` type instead.*
 *
 * @receiver The [Outcome] to collapse.
 * @return The collapsed value of the nearest common [Ancestor] type.
 *
 * @param Ancestor The nearest common ancestor type of [Ok] and [Error].
 * @param Ok The type of the [Success] value.
 * @param Error The type of the [Failure] error.
 *
 * @see Outcome.fold
 * @see Outcome.rfold
 */
@KnomadicDsl
public fun <Ancestor, Ok, Error> Outcome<Ok, Error>.collapse(): Ancestor where
        Ancestor : Any,
        Ok : Ancestor,
        Error : Ancestor
{
    return fold(success = ::itself, failure = ::itself)
}
