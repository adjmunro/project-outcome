@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@NomadicDsl
infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrDefault(default: Ok): Ok {
    return when (this@getOrDefault) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> default
    }
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrElse(
    @BuilderInference recover: (Error) -> Ok,
): Ok {
    contract {
        callsInPlace(recover, AT_MOST_ONCE)
    }

    return when (this@getOrElse) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> recover(error)
    }
}

@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrNull(): Ok? {
    contract {
        returnsNotNull() implies (this@getOrNull is Outcome.Success<Ok>)
        returns(null) implies (this@getOrNull is Outcome.Failure<Error>)
    }

    return when (this@getOrNull) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> null
    }
}

@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(): Ok {
    contract {
        returns() implies (this@getOrThrow is Outcome.Success<Ok>)
    }

    return when (this@getOrThrow) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> throw error as? Throwable
            ?: error("Outcome::getOrThrow threw! Got: $this")
    }
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(
    @BuilderInference throws: (Error) -> Throwable,
): Ok {
    contract {
        returns() implies (this@getOrThrow is Outcome.Success<Ok>)
        callsInPlace(throws, AT_MOST_ONCE)
    }

    return when (this@getOrThrow) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> throw throws(error)
    }
}

@NomadicDsl
infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrDefault(default: Error): Error {
    return when (this@errorOrDefault) {
        is Outcome.Success<Ok> -> default
        is Outcome.Failure<Error> -> error
    }
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrElse(
    @BuilderInference transform: (Ok) -> Error,
): Error {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }

    return when (this@errorOrElse) {
        is Outcome.Success<Ok> -> transform(value)
        is Outcome.Failure<Error> -> error
    }
}

@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrNull(): Error? {
    contract {
        returnsNotNull() implies (this@errorOrNull is Outcome.Failure<Error>)
        returns(null) implies (this@errorOrNull is Outcome.Success<Ok>)
    }

    return when (this@errorOrNull) {
        is Outcome.Success<Ok> -> null
        is Outcome.Failure<Error> -> error
    }
}

@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrThrow(): Error {
    contract {
        returns() implies (this@errorOrThrow is Outcome.Failure<Error>)
    }

    return when (this@errorOrThrow) {
        is Outcome.Success<Ok> -> error("Outcome::errorOrThrow threw! Got: $this")
        is Outcome.Failure<Error> -> error
    }
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrThrow(
    @BuilderInference throws: (Ok) -> Throwable,
): Error {
    contract {
        returns() implies (this@errorOrThrow is Outcome.Failure<Error>)
        callsInPlace(throws, AT_MOST_ONCE)
    }

    return when (this@errorOrThrow) {
        is Outcome.Success<Ok> -> throw throws(value)
        is Outcome.Failure<Error> -> error
    }
}
