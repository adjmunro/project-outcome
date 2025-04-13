package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.failureOf
import nz.adjmunro.knomadic.outcome.successOf
import nz.adjmunro.knomadic.outcome.outcomeOf
import nz.adjmunro.knomadic.raise.RaiseScope

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Success] value.
 *
 * - Transforms `Outcome<In, Error>` into `Outcome<Out, Error>`.
 * - If the receiver [Outcome] is an [Outcome.Failure], the `Error` is simply re-wrapped to update the `Ok` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [outcomeOf] within the transformation lambda for that.
 * - Unlike [mapSuccess][Outcome.mapSuccess], flatMapSuccess's transform lambda returns the [Outcome] wrapper directly instead of the monad's internal value.
 *
 * @receiver The [Outcome]<[In], [Error]> to transform.
 * @return A new [Outcome]<[Out], [Error]> with the transformed value.
 *
 * @param In The `Ok` type of the receiver [Outcome].
 * @param Out The `Ok` type of the returned [Outcome].
 * @param Error The `Error` type of [Outcome.Failure].
 *
 * @param transform The transform function to convert an [In] value into an [Outcome].
 *
 * @see flatMapFailure
 * @see Outcome.fold
 */
@KnomadicDsl
public inline infix fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.flatMapSuccess(
    @BuilderInference transform: (In) -> Outcome<Out, Error>,
): Outcome<Out, Error> = fold(success = transform, failure = ::failureOf)

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Failure] error.
 *
 * - Transforms `Outcome<Ok, ErrorIn>` into `Outcome<Ok, ErrorOut>`.
 * - If the receiver [Outcome] is an [Outcome.Success], the `Ok` is simply re-wrapped to update the `Error` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [outcomeOf] within the transformation lambda for that.
 * - Unlike [mapFailure][Outcome.mapFailure], flatMapFailure's transform lambda returns the [Outcome] wrapper directly instead of the monad's internal value.
 *
 * @receiver The [Outcome]<[Ok], [ErrorIn]> to transform.
 * @return A new [Outcome]<[Ok], [ErrorOut]> with the transformed error.
 *
 * @param Ok The `Ok` type of [Outcome.Success].
 * @param ErrorIn The `Error` type of the receiver [Outcome].
 * @param ErrorOut The `Error` type of the returned [Outcome].
 *
 * @param transform The transform function to convert an [ErrorIn] value into an [Outcome].
 *
 * @see flatMapSuccess
 * @see Outcome.fold
 */
@KnomadicDsl
public inline infix fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.flatMapFailure(
    @BuilderInference transform: (ErrorIn) -> Outcome<Ok, ErrorOut>,
): Outcome<Ok, ErrorOut> = fold(success = ::successOf, failure = transform)
