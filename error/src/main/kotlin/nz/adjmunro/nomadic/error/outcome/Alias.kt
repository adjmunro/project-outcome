@file:OptIn(ExperimentalTypeInference::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.experimental.ExperimentalTypeInference

/**
 * An overloaded alias for [Outcome.mapSuccess].
 *
 * @see Outcome.mapSuccess
 * @see Outcome.mapFailure
 */
@NomadicDsl
suspend inline infix fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.map(
    @BuilderInference crossinline success: suspend (In) -> Out,
): Outcome<Out, Error> = mapSuccess(success)

/**
 * An overloaded alias for [Outcome.mapFailure].
 *
 * @see Outcome.mapSuccess
 * @see Outcome.mapFailure
 */
@NomadicDsl
suspend inline infix fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.map(
    @BuilderInference crossinline failure: suspend (ErrorIn) -> ErrorOut,
): Outcome<Ok, ErrorOut> = mapFailure(failure)

/**
 * An overloaded alias for [Outcome.flatMapSuccess].
 *
 * @see Outcome.flatMapSuccess
 * @see Outcome.flatMapFailure
 */
@NomadicDsl
suspend inline infix fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.flatMap(
    @BuilderInference crossinline success: suspend (In) -> Outcome<Out, Error>,
): Outcome<Out, Error> = flatMapSuccess(success)

/**
 * An overloaded alias for [Outcome.flatMapFailure].
 *
 * @see Outcome.flatMapSuccess
 * @see Outcome.flatMapFailure
 */
@NomadicDsl
suspend inline infix fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.flatMap(
    @BuilderInference crossinline failure: suspend (ErrorIn) -> Outcome<Ok, ErrorOut>,
): Outcome<Ok, ErrorOut> = flatMapFailure(failure)

/** An alias for [Outcome.coerceToFailure]. */
@NomadicDsl
suspend inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.falter(
    @BuilderInference crossinline transform: suspend (Ok) -> Error,
): Outcome.Failure<Error> = coerceToFailure(transform)

/** An alias for [Outcome.coerceToSuccess]. */
@NomadicDsl
suspend inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.recover(
    @BuilderInference crossinline transform: suspend (Error) -> Ok,
): Outcome.Success<Ok> = coerceToSuccess(transform)

/**
 * An overloaded alias for [Outcome.collapseToAncestor].
 *
 * @see Outcome.collapseToAncestor
 * @see Outcome.collapseFold
 */
@NomadicDsl
fun <Ancestor, Ok, Error> Outcome<Ok, Error>.collapse(): Ancestor where
        Ancestor : Any, Ok : Ancestor, Error : Ancestor = collapseToAncestor()

/**
 * An overloaded alias for [Outcome.collapseFold].
 *
 * @see Outcome.collapseToAncestor
 * @see Outcome.collapseFold
 */
@NomadicDsl
suspend inline fun <Ok, Error, Output> Outcome<Ok, Error>.collapseToAncestor(
    @BuilderInference crossinline success: suspend (Ok) -> Output,
    @BuilderInference crossinline failure: suspend (Error) -> Output,
): Output where Ok : Any, Error : Any, Output : Any? = collapseFold(success, failure)

/**
 * An overloaded alias for [Outcome.flattenNestedSuccess].
 *
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@NomadicDsl
val <Ok, EmbeddedError, OuterError, AncestorError> Outcome<Outcome<Ok, EmbeddedError>, OuterError>.flatten: Outcome<Ok, AncestorError> where
        Ok : Any, AncestorError : Any, EmbeddedError : AncestorError, OuterError : AncestorError
    get() = flattenNestedSuccess()

/**
 * An overloaded alias for [Outcome.flattenNestedFailure].
 *
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@NomadicDsl
val <OuterOk, EmbeddedOk, Error, AncestorOk> Outcome<OuterOk, Outcome<EmbeddedOk, Error>>.flatten: Outcome<AncestorOk, Error> where
        AncestorOk : Any, Error : Any, OuterOk : AncestorOk, EmbeddedOk : AncestorOk
    get() = flattenNestedFailure()

/**
 * An overloaded alias for [Outcome.flattenNestedBoth].
 *
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@NomadicDsl
val <SuccessOk, SuccessError, FailureOk, FailureError, AncestorOk, AncestorError> Outcome<Outcome<SuccessOk, SuccessError>, Outcome<FailureOk, FailureError>>.flatten: Outcome<AncestorOk, AncestorError> where
        AncestorOk : Any, AncestorError : Any, SuccessOk : AncestorOk, SuccessError : AncestorError, FailureOk : AncestorOk, FailureError : AncestorError
    get() = flattenNestedBoth()
