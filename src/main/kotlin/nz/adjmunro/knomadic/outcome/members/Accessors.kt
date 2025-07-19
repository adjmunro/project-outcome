package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.inline.itself
import nz.adjmunro.knomadic.inline.nulls
import nz.adjmunro.knomadic.inline.rethrow
import nz.adjmunro.knomadic.inline.throwfold
import java.lang.IllegalStateException
import kotlin.contracts.contract

/** @return The [value][Outcome.Success] or [default]. */
@KnomadicDsl
public infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrDefault(default: Ok): Ok {
    return fold(success = ::itself, failure = { default })
}

/** @return The [value][Outcome.Success] or the result of [recover]. */
@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrElse(
    recover: (Error) -> Ok,
): Ok {
    return fold(success = ::itself, failure = recover)
}

/** @return The [value][Outcome.Success] or `null`. */
@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrNull(): Ok? {
    contract {
        returnsNotNull() implies (this@getOrNull is Outcome.Success<Ok>)
        returns(null) implies (this@getOrNull is Outcome.Failure<Error>)
    }

    return fold(success = ::itself, failure = ::nulls)
}

/**
 * @return The [value][Outcome.Success] or `throws`.
 * @throws IllegalStateException if the [Outcome] is a [failure][Outcome.Failure].
 */
@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(): Ok {
    contract { returns() implies (this@getOrThrow is Outcome.Success<Ok>) }

    return fold(success = ::itself) {
        it.throwfold(throws = ::rethrow) { error("Outcome::getOrThrow threw! Got: $this") }
    }
}

/** @return The [error][Outcome.Failure] or [default]. */
@KnomadicDsl
public infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrDefault(default: Error): Error {
    return rfold(failure = ::itself) { default }
}

/** @return The [error][Outcome.Failure] or the result of [faulter]. */
@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrElse(
    faulter: (Ok) -> Error,
): Error {
    return fold(success = faulter, failure = ::itself)
}

/** @return The [error][Outcome.Failure] or `null`. */
@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrNull(): Error? {
    contract {
        returnsNotNull() implies (this@errorOrNull is Outcome.Failure<Error>)
        returns(null) implies (this@errorOrNull is Outcome.Success<Ok>)
    }

    return fold(success = ::nulls, failure = ::itself)
}

/**
 * @return The [error][Outcome.Failure] or `throws`.
 * @throws IllegalStateException if the [Outcome] is a [success][Outcome.Success].
 */
@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.errorOrThrow(): Error {
    contract {
        returns() implies (this@errorOrThrow is Outcome.Failure<Error>)
    }

    return rfold(failure = ::itself) {
        it.throwfold(throws = ::rethrow) { error("Outcome::errorOrThrow threw! Got: $this") }
    }
}

/**
 * Attempt to unwrap [Outcome] into an [Ok] value.
 *
 * ```kotlin
 * val outcome: Outcome<String, Throwable> = ...
 * outcome.unwrap()         // getOrThrow() (default behaviour)
 * outcome.unwrap { null }  // getOrNull() (map Error to null)
 * outcome.unwrap { "$it" } // getOrElse() (map Error to fallback value)
 * ```
 *
 * @return The [value][Outcome.Success] or the result of [recover].
 * @throws IllegalStateException if the [Outcome] is a [failure][Outcome.Failure].
 * @see Outcome.getOrThrow
 * @see Outcome.getOrNull
 * @see Outcome.getOrElse
 * @see Outcome.unwrapError
 */
@KnomadicDsl
public inline infix fun <Ok, Error : Any> Outcome<Ok & Any, Error>.unwrap(
    recover: (Error) -> Ok = { error("Outcome::unwrap threw! Got: $this") },
): Ok {
    return fold(success = ::itself, failure = recover)
}

/**
 * Attempt to unwrap [Outcome] into an [Ok] value.
 *
 * ```kotlin
 * val outcome: Outcome<String, Throwable> = ...
 * outcome.unwrapError()         // errorOrThrow() (default behaviour)
 * outcome.unwrapError { null }  // errorOrNull() (map Ok to null)
 * outcome.unwrapError { "$it" } // errorOrElse() (map Ok to fallback value)
 * ```
 *
 * @return The [error][Outcome.Failure] or the result of [faulter].
 * @throws IllegalStateException if the [Outcome] is a [success][Outcome.Success].
 * @see Outcome.errorOrThrow
 * @see Outcome.errorOrNull
 * @see Outcome.errorOrElse
 * @see Outcome.unwrap
 */
@KnomadicDsl
public inline infix fun <Ok: Any, Error> Outcome<Ok, Error & Any>.unwrapError(
    faulter: (Ok) -> Error = { error("Outcome::unwrapError threw! Got: $this") },
): Error {
    return fold(success = faulter, failure = ::itself)
}
