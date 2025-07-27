@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.inline.nullfold
import nz.adjmunro.inline.throwfold

@OutcomeDsl
public inline fun <Ok : Any> successOf(block: () -> Ok): Success<Ok> = Success(value = block())

@OutcomeDsl
public inline fun <Error : Any> failureOf(block: () -> Error): Failure<Error> = Failure(error = block())

@OutcomeDsl
public fun <T> T.wrapOutcome(): Outcome<T & Any, Throwable> =
    throwfold(throws = ::Failure) { it.nullfold(none = ::Failure, some = ::Success) }
