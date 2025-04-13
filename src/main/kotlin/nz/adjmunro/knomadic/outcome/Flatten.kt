package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.util.itself

/**
 * An overloaded alias for [Outcome.flattenNestedSuccess].
 *
 * @param Ok The [value][Outcome.Success.value] type.
 * @param Err The ancestor of [Eri] & [Ero].
 * @param Eri The inner [error][Outcome.Failure.error] type.
 * @param Ero The outer [error][Outcome.Failure.error] type.
 *
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@KnomadicDsl
@get:JvmName("flattenNestedSuccessAlias")
public val <Ok, Err, Eri, Ero> Outcome<Outcome<Ok, Eri>, Ero>.flatten: Outcome<Ok, Err> where
        Ok : Any, Err : Any, Eri : Err, Ero : Err
    get() = flattenNestedSuccess()

/**
 * An overloaded alias for [Outcome.flattenNestedFailure].
 *
 * @param Ok The ancestor of [Oki] & [Oko].
 * @param Err The [error][Outcome.Failure.error] type.
 * @param Oki The inner [value][Outcome.Success.value] type.
 * @param Oko The outer [value][Outcome.Success.value] type.
 *
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@KnomadicDsl
@get:JvmName("flattenNestedFailureAlias")
public val <Ok, Err, Oki, Oko> Outcome<Oko, Outcome<Oki, Err>>.flatten: Outcome<Ok, Err> where
        Ok : Any, Err : Any, Oko : Ok, Oki : Ok
    get() = flattenNestedFailure()

/**
 * An overloaded alias for [Outcome.flattenNestedBoth].
 *
 * @param Ok The ancestor of [Oks] & [Okf].
 * @param Err The ancestor of [Ers] & [Erf].
 * @param Oks The success [value][Outcome.Success.value] type.
 * @param Ers The success [error][Outcome.Failure.error] type.
 * @param Okf The failure [value][Outcome.Success.value] type.
 * @param Erf The failure [error][Outcome.Failure.error] type.
 *
 * @see Outcome.flattenNestedSuccess
 * @see Outcome.flattenNestedFailure
 * @see Outcome.flattenNestedBoth
 */
@KnomadicDsl
@get:JvmName("flattenNestedBothAlias")
public val <Ok, Err, Oks, Ers, Okf, Erf> Outcome<Outcome<Oks, Ers>, Outcome<Okf, Erf>>.flatten: Outcome<Ok, Err> where
        Ok : Any, Err : Any, Oks : Ok, Ers : Err, Okf : Ok, Erf : Err
    get() = flattenNestedBoth()

/**
 * Flatten a nested [Outcome] inside the [Outcome.Success] state, into a single [Outcome].
 *
 * *The `Error` type of the returned [Outcome] will be the nearest common [AncestorError] of
 * [EmbeddedError] and [OuterError].*
 *
 * @receiver The [Outcome.Success] of an [Outcome]<[Ok], [EmbeddedError]> to flatten.
 * @return The flattened [Outcome]<[Ok], [AncestorError]>.
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
        Ok : Any,
        AncestorError : Any,
        EmbeddedError : AncestorError,
        OuterError : AncestorError
{
    return fold(success = ::itself, failure = ::failureOf)
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
        AncestorOk : Any,
        Error : Any,
        OuterOk : AncestorOk,
        EmbeddedOk : AncestorOk
{
    return fold(success = ::successOf, failure = ::itself)
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
        AncestorOk : Any,
        AncestorError : Any,
        SuccessOk : AncestorOk,
        SuccessError : AncestorError,
        FailureOk : AncestorOk,
        FailureError : AncestorError
{
    return fold(success = ::itself, failure = ::itself)
}
