@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.inline.nullfold
import nz.adjmunro.inline.throwfold
import nz.adjmunro.knomadic.outcome.Failure
import nz.adjmunro.knomadic.outcome.Faulty
import nz.adjmunro.knomadic.outcome.Maybe
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.OutcomeDsl
import nz.adjmunro.knomadic.outcome.Success
import nz.adjmunro.knomadic.util.nonFatalOrThrow

@OutcomeDsl
public fun <T> T.wrapOutcome(): Outcome<T & Any, Throwable> =
    throwfold(throws = ::Failure) { it.nullfold(none = ::Failure, some = ::Success) }

@OutcomeDsl
public inline fun <T> T.wrapMaybe(): Maybe<T & Any> =
    throwfold(throws = ::emptyFailure) { it.nullfold(none = ::emptyFailure, some = ::Success) }

@OutcomeDsl
public inline fun <Error : Throwable> Error.wrapFaulty(): Faulty<Error> = Failure(error = nonFatalOrThrow())

@OutcomeDsl
public inline fun <T> T.wrapFaulty(): Faulty<Throwable> =
    throwfold(throws = ::Failure) { it.nullfold(none = ::Failure, some = ::emptySuccess) }
