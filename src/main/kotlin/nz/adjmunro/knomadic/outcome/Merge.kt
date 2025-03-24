package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.raise.RaiseScope
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

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
public suspend inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.coerceToFailure(
    @BuilderInference crossinline falter: suspend (Ok) -> Error,
): Outcome.Failure<Error> {
    contract { callsInPlace(falter, AT_MOST_ONCE) }

    return when (this@coerceToFailure) {
        is Outcome.Success<Ok> -> Outcome.Failure(falter(value))
        is Outcome.Failure<Error> -> this@coerceToFailure
    }
}

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
public suspend inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.coerceToSuccess(
    @BuilderInference crossinline recover: suspend (Error) -> Ok,
): Outcome.Success<Ok> {
    contract { callsInPlace(recover, AT_MOST_ONCE) }

    return when (this@coerceToSuccess) {
        is Outcome.Success<Ok> -> this@coerceToSuccess
        is Outcome.Failure<Error> -> Outcome.Success(recover(error))
    }
}

/**
 * Collapse the receiver [Outcome] into either [value][Outcome.Success.value] or
 * [error][Outcome.Failure.error], using the nearest common [Ancestor] as the type.
 *
 * *Use [Outcome.collapseFold] with `success` and `failure` lambda arguments
 * to directly map each state to a specific `Output` type instead.*
 *
 * @receiver The [Outcome] to collapse.
 * @return The collapsed value of the nearest common [Ancestor] type.
 *
 * @param Ancestor The nearest common ancestor type of [Ok] and [Error].
 * @param Ok The type of the [Outcome.Success] value.
 * @param Error The type of the [Outcome.Failure] error.
 *
 * @see Outcome.collapse
 * @see Outcome.collapseFold
 */
@KnomadicDsl
public fun <Ancestor, Ok, Error> Outcome<Ok, Error>.collapseToAncestor(): Ancestor where
        Ancestor : Any, Ok : Ancestor, Error : Ancestor {
    return when (this@collapseToAncestor) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> error
    }
}

/**
 * Collapse the receiver [Outcome] into an instance of [Output].
 *
 * *Use [Outcome.collapseToAncestor], to assume the nearest common `Ancestor` as the [Output] type.*
 *
 * @receiver The [Outcome] to collapse.
 * @return The collapsed value of type [Output].
 *
 * @param Ok The type of the [Outcome.Success] value.
 * @param Error The type of the [Outcome.Failure] error.
 *
 * @param success The lambda to transform the [Outcome.Success] value into [Output].
 * @param failure The lambda to transform the [Outcome.Failure] error into [Output].
 *
 * @see Outcome.collapse
 * @see Outcome.collapseToAncestor
 */
@KnomadicDsl
public suspend inline fun <Ok, Error, Output> Outcome<Ok, Error>.collapseFold(
    @BuilderInference crossinline success: suspend (Ok) -> Output,
    @BuilderInference crossinline failure: suspend (Error) -> Output,
): Output where Ok : Any, Error : Any, Output : Any? {
    contract {
        callsInPlace(success, AT_MOST_ONCE)
        callsInPlace(failure, AT_MOST_ONCE)
    }

    return when (this@collapseFold) {
        is Outcome.Success<Ok> -> success(value)
        is Outcome.Failure<Error> -> failure(error)
    }
}

/**
 * Flatten a nested [Outcome] inside the [Outcome.Success] state, into a single [Outcome].
 *
 * *The `Error` type of the returned [Outcome] will be the nearest common [AncestorError] of
 * [EmbeddedError] and [OuterError].*
 *
 * @receiver The [Outcome.Success] of an [Outcome]<[Ok], [EmbeddedError]> to flatten.
 * @return The flattened [Outcome]<[Ok], [AncestorError].
 *
 * @param Ok The type of the [Outcome.Success] value.
 * @param EmbeddedError The type of the [Outcome.Failure] error nested inside the [Outcome.Success].
 * @param OuterError The type of the non-nested [Outcome.Failure] error.
 * @param AncestorError The nearest common ancestor type of [EmbeddedError] and [OuterError].
 *
 * @see Outcome.flatten
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@KnomadicDsl
public fun <Ok, EmbeddedError, OuterError, AncestorError> Outcome<Outcome<Ok, EmbeddedError>, OuterError>.flattenNestedSuccess(): Outcome<Ok, AncestorError> where
        Ok : Any, AncestorError : Any, EmbeddedError : AncestorError, OuterError : AncestorError {
    return when (this@flattenNestedSuccess) {
        is Outcome.Success<Outcome<Ok, EmbeddedError>> -> when (value) {
            is Outcome.Success<Ok> -> Outcome.Success(value.value)
            is Outcome.Failure<EmbeddedError> -> Outcome.Failure(value.error)
        }

        is Outcome.Failure<OuterError> -> Outcome.Failure(error)
    }
}

/**
 * Flatten a nested [Outcome] inside the [Outcome.Failure] state, into a single [Outcome].
 *
 * *The `Ok` type of the returned [Outcome] will be the nearest common [AncestorOk] of
 * [EmbeddedOk] and [OuterOk].*
 *
 * @receiver The [Outcome.Failure] of an [Outcome]<[EmbeddedOk], [Error]> to flatten.
 * @return The flattened [Outcome]<[AncestorOk], [Error]>.
 *
 * @param Error The type of the [Outcome.Failure] error.
 * @param OuterOk The type of the non-nested [Outcome.Success] value.
 * @param EmbeddedOk The type of the [Outcome.Success] value nested inside the [Outcome.Failure].
 * @param AncestorOk The nearest common ancestor type of [EmbeddedOk] and [OuterOk].
 *
 * @see Outcome.flatten
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedBoth
 */
@KnomadicDsl
public fun <OuterOk, EmbeddedOk, Error, AncestorOk> Outcome<OuterOk, Outcome<EmbeddedOk, Error>>.flattenNestedFailure(): Outcome<AncestorOk, Error> where
        AncestorOk : Any, Error : Any, OuterOk : AncestorOk, EmbeddedOk : AncestorOk {
    return when (this@flattenNestedFailure) {
        is Outcome.Success<OuterOk> -> Outcome.Success(value)

        is Outcome.Failure<Outcome<EmbeddedOk, Error>> -> when (error) {
            is Outcome.Success<EmbeddedOk> -> Outcome.Success(error.value)
            is Outcome.Failure<Error> -> Outcome.Failure(error.error)
        }
    }
}

/**
 * Flatten the nested [Outcome] inside both [Outcome.Success] and [Outcome.Failure] states, into a single [Outcome].
 *
 * *The `Ok` and `Error` types of the returned [Outcome] will be the nearest common [AncestorOk] and [AncestorError].*
 *
 * @receiver The [Outcome] to flatten, with a nested [Outcome] inside both success and failure states.
 * @return The flattened [Outcome]<[AncestorOk], [AncestorError]>.
 *
 * @param SuccessOk The type of the [Outcome.Success] value nested inside the [Outcome.Success].
 * @param SuccessError The type of the [Outcome.Failure] error nested inside the [Outcome.Success].
 * @param FailureOk The type of the [Outcome.Success] value nested inside the [Outcome.Failure].
 * @param FailureError The type of the [Outcome.Failure] error nested inside the [Outcome.Failure].
 * @param AncestorOk The nearest common ancestor type of [SuccessOk] and [FailureOk].
 * @param AncestorError The nearest common ancestor type of [SuccessError] and [FailureError].
 *
 * @see Outcome.flatten
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 */
@KnomadicDsl
public fun <SuccessOk, SuccessError, FailureOk, FailureError, AncestorOk, AncestorError> Outcome<Outcome<SuccessOk, SuccessError>, Outcome<FailureOk, FailureError>>.flattenNestedBoth(): Outcome<AncestorOk, AncestorError> where
        AncestorOk : Any, AncestorError : Any, SuccessOk : AncestorOk, SuccessError : AncestorError, FailureOk : AncestorOk, FailureError : AncestorError {
    return when (this@flattenNestedBoth) {
        is Outcome.Success<Outcome<SuccessOk, SuccessError>> -> when (value) {
            is Outcome.Success<SuccessOk> -> Outcome.Success(value.value)
            is Outcome.Failure<SuccessError> -> Outcome.Failure(value.error)
        }

        is Outcome.Failure<Outcome<FailureOk, FailureError>> -> when (error) {
            is Outcome.Success<FailureOk> -> Outcome.Success(error.value)
            is Outcome.Failure<FailureError> -> Outcome.Failure(error.error)
        }
    }
}
