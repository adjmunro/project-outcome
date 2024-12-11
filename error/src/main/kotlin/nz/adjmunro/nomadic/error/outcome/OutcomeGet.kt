package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeGet {

    @NomadicDsl
    fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrDefault(default: Ok): Ok {
        return when (this@getOrDefault) {
            is Outcome.Success<Ok> -> value
            is Outcome.Failure<Error> -> default
        }
    }

    @NomadicDsl
    inline fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrElse(
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
    fun <Ok : Any, Error : Any> Outcome<Ok, Error>.getOrThrow(
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

}
