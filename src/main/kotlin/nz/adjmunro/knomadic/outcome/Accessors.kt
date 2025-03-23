package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

@KnomadicDsl
public infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrDefault(default: Ok): Ok {
    return when (this@getOrDefault) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> default
    }
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrElse(
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

@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrNull(): Ok? {
    contract {
        returnsNotNull() implies (this@getOrNull is Outcome.Success<Ok>)
        returns(null) implies (this@getOrNull is Outcome.Failure<Error>)
    }

    return when (this@getOrNull) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> null
    }
}

@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(): Ok {
    contract {
        returns() implies (this@getOrThrow is Outcome.Success<Ok>)
    }

    return when (this@getOrThrow) {
        is Outcome.Success<Ok> -> value
        is Outcome.Failure<Error> -> throw error as? Throwable
            ?: error("Outcome::getOrThrow threw! Got: $this")
    }
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(
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

@KnomadicDsl
public infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrDefault(default: Error): Error {
    return when (this@errorOrDefault) {
        is Outcome.Success<Ok> -> default
        is Outcome.Failure<Error> -> error
    }
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrElse(
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

@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrNull(): Error? {
    contract {
        returnsNotNull() implies (this@errorOrNull is Outcome.Failure<Error>)
        returns(null) implies (this@errorOrNull is Outcome.Success<Ok>)
    }

    return when (this@errorOrNull) {
        is Outcome.Success<Ok> -> null
        is Outcome.Failure<Error> -> error
    }
}

@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrThrow(): Error {
    contract {
        returns() implies (this@errorOrThrow is Outcome.Failure<Error>)
    }

    return when (this@errorOrThrow) {
        is Outcome.Success<Ok> -> error("Outcome::errorOrThrow threw! Got: $this")
        is Outcome.Failure<Error> -> error
    }
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrThrow(
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
