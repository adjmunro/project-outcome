@file:Suppress("NOTHING_TO_INLINE") @file:OptIn(
    ExperimentalContracts::class,
    ExperimentalTypeInference::class
)

package nz.adjmunro.nomadic.error.util

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.R
import nz.adjmunro.nomadic.error.fallible.Fallible
import nz.adjmunro.nomadic.error.maybe.Maybe
import nz.adjmunro.nomadic.error.outcome.Outcome
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@NomadicDsl
inline fun <T> it(value: T): T {
    return value
}

//@Suppress("UNCHECKED_CAST")
@NomadicDsl
inline fun <T> T.receiver(): T {
    return this@receiver// as R
}

@NomadicDsl
inline fun <T> T.receiver(ignore: Any?): T {
    return this@receiver
}

@NomadicDsl
inline fun throws(throwable: Throwable): Nothing {
    throw throwable
}

@NomadicDsl
inline fun nulls(ignore: Any?): Unit? {
    return null
}

// this is possibly my dumbest idea yet... but i'm lazy and i want to see if it works
@NomadicDsl
inline fun <In, Out> In.nullfold(some: (In) -> Out, none: () -> Out): Out {
    contract {
        callsInPlace(some, AT_MOST_ONCE)
        callsInPlace(none, AT_MOST_ONCE)
    }

    return when (this@nullfold) {
        null -> none()
        else -> some(this@nullfold)
    }
}

@NomadicDsl
inline fun <Ok : Any> success(value: Ok): Outcome.Success<Ok> {
    return Outcome.Success(value)
}

@NomadicDsl
inline fun <Error : Any> failure(error: Error): Outcome.Failure<Error> {
    return Outcome.Failure(error)
}


@NomadicDsl
inline fun <Error : Any> failure(@BuilderInference error: () -> Error): Outcome.Failure<Error> {
    contract { callsInPlace(error, EXACTLY_ONCE) }
    return Outcome.Failure(error())
}


@NomadicDsl
inline fun <Ok : Any> some(value: Ok): Maybe.Some<Ok> {
    return Maybe.Some(value)
}

@NomadicDsl
inline fun none(): Maybe.None {
    return Maybe.None
}

@NomadicDsl
inline fun none(ignore: Any?): Maybe.None {
    return Maybe.None
}

@NomadicDsl
inline fun pass(): Fallible.Pass {
    return Fallible.Pass
}

@NomadicDsl
inline fun pass(ignore: Any?): Fallible.Pass {
    return Fallible.Pass
}

@NomadicDsl
inline fun <Error : Any> oops(error: Error): Fallible.Oops<Error> {
    return Fallible.Oops(error)
}

@NomadicDsl
inline fun <Error : Any> oops(@BuilderInference error: () -> Error): Fallible.Oops<Error> {
    contract { callsInPlace(error, EXACTLY_ONCE) }
    return Fallible.Oops(error())
}
