package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.failureOf
import nz.adjmunro.knomadic.outcome.successOf
import nz.adjmunro.knomadic.raise.RaiseScope
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

/** An alias for [coerceToFailure]. */
@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.falter(
    transform: (Ok) -> Error,
): Outcome.Failure<Error> = coerceToFailure(transform)

/** An alias for [coerceToSuccess]. */
@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.recover(
    transform: (Error) -> Ok,
): Outcome.Success<Ok> = coerceToSuccess(transform)

/**
 * Returns a new [Outcome.Success], after applying [recover] to the [Outcome.Failure] error.
 *
 * - Transforms `Outcome<Ok, Error>` into `Outcome.Success<Ok>`.
 * - If the receiver [Outcome] is an [Outcome.Success], nothing happens.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [outcomeOf] within the transformation lambda for that.
 *
 * @receiver The [Outcome]<[Ok], [Error]> to transform.
 * @return A new [Outcome.Success]<[Ok]> with the transformed value.
 *
 * @param Ok The `Ok` type of [Outcome.Success].
 * @param Error The `Error` type of the receiver [Outcome].
 *
 * @param recover The transform function to convert an [Error] value into an [Ok] value.
 *
 * @see Outcome.coerceToFailure
 * @see Outcome.recover
 */
@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.coerceToSuccess(
    recover: (Error) -> Ok,
): Outcome.Success<Ok> {
    contract { callsInPlace(recover, AT_MOST_ONCE) }
    return fold(success = ::successOf) { successOf(recover(it)) }
}

/**
 * Returns a new [Outcome.Failure], after applying [falter] to the [Outcome.Success] value.
 *
 * - Transforms `Outcome<Ok, Error>` into `Outcome.Failure<Ok, Error>`.
 * - If the receiver [Outcome] is an [Outcome.Failure], nothing happens.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [outcomeOf] within the transformation lambda for that.
 *
 * @receiver The [Outcome]<[Ok], [Error]> to transform.
 * @return A new [Outcome.Failure]<[Nothing], [Error]> with the transformed error.
 *
 * @param Ok The `Ok` type of the receiver [Outcome].
 * @param Error The `Error` type of [Outcome.Failure].
 *
 * @param falter The transform function to convert an [Ok] value into an [Error] value.
 *
 * @see Outcome.coerceToSuccess
 * @see Outcome.falter
 */
@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.coerceToFailure(
    falter: (Ok) -> Error,
): Outcome.Failure<Error> {
    contract { callsInPlace(falter, AT_MOST_ONCE) }

    return fold(
        success = { failureOf(falter(it)) },
        failure = ::failureOf,
    )
}
