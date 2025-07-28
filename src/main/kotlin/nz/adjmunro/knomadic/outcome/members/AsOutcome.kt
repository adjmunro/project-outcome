@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.inline.unit
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Failure
import nz.adjmunro.knomadic.outcome.Faulty
import nz.adjmunro.knomadic.outcome.Maybe
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.OutcomeDsl
import nz.adjmunro.knomadic.outcome.Success


@KnomadicDsl
public fun <T: Any> T.asSuccess(): Success<T> = Success(value = this)

@KnomadicDsl
public fun <T: Any> T.asFailure(): Failure<T> = Failure(error = this)


// TODO make params?
@OutcomeDsl
public inline fun <Error : Any> Outcome<*, Error>.asFaulty(): Faulty<Error> = mapSuccess(transform = ::unit)

@OutcomeDsl
public inline fun <Ok : Any> Outcome<Ok, *>.asMaybe(): Maybe<Ok> = mapFailure(transform = ::unit)
