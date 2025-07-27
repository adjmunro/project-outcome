package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.failureOf
import nz.adjmunro.knomadic.outcome.outcomeOf
import nz.adjmunro.knomadic.outcome.successOf
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.inline.itself
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

/**
 * Transform an [Outcome] with either [success] or [failure].
 *
 * - Unlike, [fold][Outcome.fold], `map` re-wraps each transform *as the same kind of [Outcome].*
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [andThen]/[tryRecover] or [outcomeOf] within the transformation lambdas for that.
 *
 * @receiver The [Outcome]<[In], [ErrorIn]> to transform.
 * @return A new [Outcome]<[Out], [ErrorOut]> with the transformed value.
 *
 * @param In The `Ok` type of the receiver [Outcome].
 * @param Out The `Ok` type of the returned [Outcome].
 * @param ErrorIn The `Error` type of the receiver [Outcome].
 * @param ErrorOut The `Error` type of the returned [Outcome].
 *
 * @param success The transform to apply if the receiver [Outcome] is an [Outcome.Success].
 * @param failure The transform to apply if the receiver [Outcome] is an [Outcome.Failure].
 *
 * @see Outcome.andThen
 * @see Outcome.tryRecover
 * @see Outcome.flatMapSuccess
 * @see Outcome.flatMapFailure
 */
@KnomadicDsl
public inline fun <In : Any, Out : Any, ErrorIn : Any, ErrorOut : Any> Outcome<In, ErrorIn>.map(
    success: (In) -> Out,
    failure: (ErrorIn) -> ErrorOut,
): Outcome<Out, ErrorOut> {
    contract {
        callsInPlace(success, AT_MOST_ONCE)
        callsInPlace(failure, AT_MOST_ONCE)
    }

    return fold(
        success = { successOf(success(it)) },
        failure = { failureOf(failure(it)) },
    )
}

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Success] value.
 *
 * - Transforms `Outcome<In, Error>` into `Outcome<Out, Error>`.
 * - If the receiver [Outcome] is an [Outcome.Failure], the `Error` is simply re-wrapped to update the `Ok` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [andThen] or [outcomeOf] within the transformation lambda for that.
 * - Unlike [flatMapSuccess], mapSuccess's transform lambda returns the monad's internal value directly instead of the [Outcome] wrapper.
 *
 * @receiver The [Outcome]<[In], [Error]> to transform.
 * @return A new [Outcome]<[Out], [Error]> with the transformed value.
 *
 * @param In The `Ok` type of the receiver [Outcome].
 * @param Out The `Ok` type of the returned [Outcome].
 * @param Error The `Error` type of [Outcome.Failure].
 *
 * @param transform The transform function to convert an [In] value into an [Out] value.
 *
 * @see Outcome.map
 * @see Outcome.andThen
 * @see Outcome.flatMapSuccess
 * @see Outcome.mapFailure
 */
@KnomadicDsl
public inline infix fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.mapSuccess(
    transform: (In) -> Out,
): Outcome<Out, Error> {
    return map(success = transform, failure = ::itself)
}


/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Failure] error.
 *
 * - Transforms `Outcome<Ok, ErrorIn>` into `Outcome<Ok, ErrorOut>`.
 * - If the receiver [Outcome] is an [Outcome.Success], the `Ok` is simply re-wrapped to update the `Error` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [tryRecover] or [outcomeOf] within the transformation lambda for that.
 * - Unlike [flatMapFailure], mapFailure's transform lambda returns the monad's internal value directly instead of the [Outcome] wrapper.
 *
 * @receiver The [Outcome]<[Ok], [ErrorIn]> to transform.
 * @return A new [Outcome]<[Ok], [ErrorOut]> with the transformed error.
 *
 * @param Ok The `Ok` type of [Outcome.Success].
 * @param ErrorIn The `Error` type of the receiver [Outcome].
 * @param ErrorOut The `Error` type of the returned [Outcome].
 *
 * @param transform The transform function to convert an [ErrorIn] value into an [ErrorOut] value.
 *
 * @see Outcome.map
 * @see Outcome.tryRecover
 * @see Outcome.flatMapFailure
 * @see Outcome.mapSuccess
 */
@KnomadicDsl
public inline infix fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.mapFailure(
    transform: (ErrorIn) -> ErrorOut,
): Outcome<Ok, ErrorOut> {
    return map(success = ::itself, failure = transform)
}

/**
 * Inverts the [Outcome] so that the [Outcome.Success] value becomes the [Outcome.Failure] error and vice versa.
 *
 * @param Ok The `Ok` type of the receiver [Outcome], and `Error` type of the returned [Outcome].
 * @param Error The `Error` type of the receiver [Outcome], and `Ok` type of the returned [Outcome].
 * @return A new [Outcome] with the values inverted.
 */
@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.invert(): Outcome<Error, Ok> {
    return fold(success = ::failureOf, failure = ::successOf)
}
