@file:Suppress("NOTHING_TO_INLINE") @file:OptIn(
    ExperimentalTypeInference::class, ExperimentalContracts::class
)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome.Failure
import nz.adjmunro.nomadic.error.outcome.Outcome.Success
import nz.adjmunro.nomadic.error.util.nullfold
import nz.adjmunro.nomadic.error.util.throwfold
import kotlin.contracts.ExperimentalContracts
import kotlin.experimental.ExperimentalTypeInference

@NomadicDsl
inline fun <Ok : Any> successOf(value: Ok): Success<Ok> = Success(value)

@NomadicDsl
inline fun <Error : Any> failureOf(error: Error): Failure<Error> = Failure(error)

fun <T> T.wrapOutcome(): Outcome<T & Any, Throwable> =
    throwfold(::failureOf) { it.nullfold(::failureOf, ::successOf) }
