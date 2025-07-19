@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.inline.nullfold
import nz.adjmunro.knomadic.inline.throwfold

@KnomadicDsl
public inline fun <Ok : Any> successOf(value: Ok): Outcome.Success<Ok> = Outcome.Success(value)

@KnomadicDsl
public inline fun <Error : Any> failureOf(error: Error): Outcome.Failure<Error> = Outcome.Failure(error)

@KnomadicDsl
public fun <T> T.wrapOutcome(): Outcome<T & Any, Throwable> =
    throwfold(::failureOf) { it.nullfold(::failureOf, ::successOf) }
