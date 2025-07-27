package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.inline.nulls
import nz.adjmunro.inline.rethrow
import nz.adjmunro.inline.throwfold
import nz.adjmunro.knomadic.outcome.Failure
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.OutcomeDsl
import nz.adjmunro.knomadic.outcome.Success
import kotlin.contracts.contract

/** @return The [value][Success] or [default]. */
@OutcomeDsl
public infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrDefault(default: Ok): Ok {
    return fold(success = Success<Ok>::value, failure = { default })
}

/** @return The [value][Success] or the result of [recover]. */
@OutcomeDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrElse(
    recover: (Error) -> Ok,
): Ok {
    return fold(success = Success<Ok>::value, failure = { recover(error) })
}

/** @return The [value][Success] or `null`. */
@OutcomeDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrNull(): Ok? {
    contract {
        returnsNotNull() implies (this@getOrNull is Success<Ok>)
        returns(null) implies (this@getOrNull is Failure<Error>)
    }

    return fold(success = Success<Ok>::value, failure = ::nulls)
}

/**
 * @return The [value][Success] or `throws`.
 * @throws IllegalStateException if the [Outcome] is a [failure][Failure].
 */
@OutcomeDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(): Ok {
    contract { returns() implies (this@getOrThrow is Success<Ok>) }

    return rfold(success = Success<Ok>::value) {
        error.throwfold(throws = ::rethrow) { error("Outcome::getOrThrow threw! Got: $this") }
    }
}

/**
 * Attempt to unwrap [Outcome] into an [Ok] value.
 *
 * > **Be warned: [recover] throws by default!**
 * 
 * ```kotlin
 * val outcome: Outcome<String, Throwable> = ...
 * outcome.unwrap()         // getOrThrow() (default behaviour)
 * outcome.unwrap { null }  // getOrNull() (map Error to null)
 * outcome.unwrap { "$it" } // getOrElse() (map Error to fallback value)
 * ```
 *
 * @param recover A function that maps the [Error] value to an [Ok] value.
 * @return The [value][Success.value] or the result of [recover].
 * @throws IllegalStateException if the [Outcome] is a [failure][Failure], by default.
 * @see Outcome.getOrThrow
 * @see Outcome.getOrNull
 * @see Outcome.getOrElse
 * @see Outcome.unwrapError
 */
@OutcomeDsl
public inline infix fun <Ok, Error : Any> Outcome<Ok & Any, Error>.unwrap(
    recover: (Error) -> Ok = { error("Outcome::unwrap threw! Got: $this") },
): Ok {
    return fold(success = Success<Ok & Any>::value, failure = { recover(error) })
}

/** @return The [error][Failure] or [default]. */
@OutcomeDsl
public infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrDefault(default: Error): Error {
    return fold(failure = Failure<Error>::error) { default }
}

/** @return The [error][Failure] or the result of [faulter]. */
@OutcomeDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrElse(
    faulter: (Ok) -> Error,
): Error {
    return fold(success = { faulter(value) }, failure = Failure<Error>::error)
}

/** @return The [error][Failure] or `null`. */
@OutcomeDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrNull(): Error? {
    contract {
        returnsNotNull() implies (this@errorOrNull is Failure<Error>)
        returns(null) implies (this@errorOrNull is Success<Ok>)
    }

    return fold(success = ::nulls, failure = Failure<Error>::error)
}

/**
 * @return The [error][Failure] or `throws`.
 * @throws IllegalStateException if the [Outcome] is a [success][Success].
 */
@OutcomeDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrThrow(): Error {
    contract {
        returns() implies (this@errorOrThrow is Failure<Error>)
    }

    return fold(failure = Failure<Error>::error) {
        value.throwfold(throws = ::rethrow) { error("Outcome::errorOrThrow threw! Got: $this") }
    }
}

/**
 * Attempt to unwrap [Outcome] into an [Error] value.
 *
 * > **Be warned: [faulter] throws by default!**
 * 
 * ```kotlin
 * val outcome: Outcome<String, Throwable> = ...
 * outcome.unwrapError()         // errorOrThrow() (default behaviour)
 * outcome.unwrapError { null }  // errorOrNull() (map Ok to null)
 * outcome.unwrapError { "$it" } // errorOrElse() (map Ok to fallback value)
 * ```
 *
 * @param faulter A function that maps the [Ok] value to an [Error] value.
 * @return The [error][Failure.error] or the result of [faulter].
 * @throws IllegalStateException if the [Outcome] is a [success][Success], by default.
 * @see Outcome.errorOrThrow
 * @see Outcome.errorOrNull
 * @see Outcome.errorOrElse
 * @see Outcome.unwrap
 */
@OutcomeDsl
public inline infix fun <Ok: Any, Error> Outcome<Ok, Error & Any>.unwrapError(
    faulter: (Ok) -> Error = { error("Outcome::unwrapError threw! Got: $this") },
): Error {
    return fold(success = { faulter(value) }, failure = Failure<Error & Any>::error)
}
