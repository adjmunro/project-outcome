@file:OptIn(ExperimentalTypeInference::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.util.it
import nz.adjmunro.nomadic.error.util.outcomeFailure
import nz.adjmunro.nomadic.error.util.outcomeSuccess
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

/**
 * Returns a new [Outcome], after applying [success] or [failure] based on the current [Outcome] state.
 *
 * - Transforms `Outcome<In, ErrorIn>` into `Outcome<Out, ErrorOut>`.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [Outcome.outcomeOf] within the transformation lambdas for that.
 * - Unlike [Outcome.flatFold], fold's transform lambdas return the monad's internal value directly instead of the [Outcome] wrapper.
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
 * @see flatFold
 * @see mapEachSuccess
 * @see mapEachFailure
 */
@OptIn(ExperimentalContracts::class)
@NomadicDsl
suspend inline fun <In : Any, Out : Any, ErrorIn : Any, ErrorOut : Any> Outcome<In, ErrorIn>.fold(
    @BuilderInference crossinline success: suspend (In) -> Out,
    @BuilderInference crossinline failure: suspend (ErrorIn) -> ErrorOut,
): Outcome<Out, ErrorOut> {
    contract {
        callsInPlace(success, AT_MOST_ONCE)
        callsInPlace(failure, AT_MOST_ONCE)
    }

    return when (this@fold) {
        is Outcome.Success<In> -> Outcome.Success(success(value))
        is Outcome.Failure<ErrorIn> -> Outcome.Failure(failure(error))
    }
}

/**
 * Returns a new [Outcome], after applying [success] or [failure] based on the current [Outcome] state.
 *
 * - Transforms `Outcome<In, ErrorIn>` into `Outcome<Out, ErrorOut>`.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [Outcome.outcomeOf] within the transformation lambdas for that.
 * - Unlike [Outcome.fold], flatFold's transform lambdas return the [Outcome] wrapper directly instead of the monad's internal value.
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
 * @see fold
 * @see flatMapSuccess
 * @see flatMapFailure
 */
@OptIn(ExperimentalContracts::class)
@NomadicDsl
suspend inline fun <In : Any, Out : Any, ErrorIn : Any, ErrorOut : Any> Outcome<In, ErrorIn>.flatFold(
    @BuilderInference crossinline success: suspend (In) -> Outcome<Out, ErrorOut>,
    @BuilderInference crossinline failure: suspend (ErrorIn) -> Outcome<Out, ErrorOut>,
): Outcome<Out, ErrorOut> {
    contract {
        callsInPlace(success, AT_MOST_ONCE)
        callsInPlace(failure, AT_MOST_ONCE)
    }

    return when (this@flatFold) {
        is Outcome.Success<In> -> success(value)
        is Outcome.Failure<ErrorIn> -> failure(error)
    }
}

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Success] value.
 *
 * - Transforms `Outcome<In, Error>` into `Outcome<Out, Error>`.
 * - If the receiver [Outcome] is an [Outcome.Failure], the `Error` is simply re-wrapped to update the `Ok` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [Outcome.outcomeOf] within the transformation lambda for that.
 * - Unlike [Outcome.flatMapSuccess], mapSuccess's transform lambda returns the monad's internal value directly instead of the [Outcome] wrapper.
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
 * @see Outcome.mapFailure
 * @see Outcome.fold
 */
@NomadicDsl
suspend inline infix fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.mapSuccess(
    @BuilderInference crossinline transform: suspend (In) -> Out,
): Outcome<Out, Error> = fold(success = transform, failure = ::it)

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Success] value.
 *
 * - Transforms `Outcome<In, Error>` into `Outcome<Out, Error>`.
 * - If the receiver [Outcome] is an [Outcome.Failure], the `Error` is simply re-wrapped to update the `Ok` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [Outcome.outcomeOf] within the transformation lambda for that.
 * - Unlike [Outcome.mapSuccess], flatMapSuccess's transform lambda returns the [Outcome] wrapper directly instead of the monad's internal value.
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
 * @see Outcome.flatMapFailure
 * @see Outcome.flatFold
 */
@NomadicDsl
suspend inline infix fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.flatMapSuccess(
    @BuilderInference crossinline transform: suspend (In) -> Outcome<Out, Error>,
): Outcome<Out, Error> = flatFold(success = transform, failure = ::outcomeFailure)

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Failure] error.
 *
 * - Transforms `Outcome<Ok, ErrorIn>` into `Outcome<Ok, ErrorOut>`.
 * - If the receiver [Outcome] is an [Outcome.Success], the `Ok` is simply re-wrapped to update the `Error` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [Outcome.outcomeOf] within the transformation lambda for that.
 * - Unlike [Outcome.flatMapFailure], mapFailure's transform lambda returns the monad's internal value directly instead of the [Outcome] wrapper.
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
 * @see Outcome.mapSuccess
 * @see Outcome.fold
 */
@NomadicDsl
suspend inline infix fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.mapFailure(
    @BuilderInference crossinline transform: suspend (ErrorIn) -> ErrorOut,
): Outcome<Ok, ErrorOut> = fold(success = ::it, failure = transform)

/**
 * Returns a new [Outcome], after applying [transform] to the [Outcome.Failure] error.
 *
 * - Transforms `Outcome<Ok, ErrorIn>` into `Outcome<Ok, ErrorOut>`.
 * - If the receiver [Outcome] is an [Outcome.Success], the `Ok` is simply re-wrapped to update the `Error` type.
 * - This function **does not** provide a [RaiseScope], and ***makes no guarantees*** about catching,
 *   handling, or rethrowing errors! Use [Outcome.outcomeOf] within the transformation lambda for that.
 * - Unlike [Outcome.mapFailure], flatMapFailure's transform lambda returns the [Outcome] wrapper directly instead of the monad's internal value.
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
 * @see Outcome.flatMapSuccess
 * @see Outcome.flatFold
 */
@NomadicDsl
suspend inline infix fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.flatMapFailure(
    @BuilderInference crossinline transform: suspend (ErrorIn) -> Outcome<Ok, ErrorOut>,
): Outcome<Ok, ErrorOut> = flatFold(success = ::outcomeSuccess, failure = transform)

/**
 * Inverts the [Outcome] so that the [Outcome.Success] value becomes the [Outcome.Failure] error and vice versa.
 *
 * @receiver The [Outcome] to invert.
 * @return A new [Outcome] with the values inverted.
 *
 * @param Ok The `Ok` type of the receiver [Outcome], and `Error` type of the returned [Outcome].
 * @param Error The `Error` type of the receiver [Outcome], and `Ok` type of the returned [Outcome].
 *
 * @see Outcome.coerceToSuccess
 * @see Outcome.coerceToFailure
 */
@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.invert(): Outcome<Error, Ok> {
    return when (this@invert) {
        is Outcome.Success<Ok> -> Outcome.Failure(value)
        is Outcome.Failure<Error> -> Outcome.Success(error)
    }
}
