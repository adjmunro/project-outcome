package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object MaybeGet {

    @NomadicDsl
    infix fun <Ok : Any> Maybe<Ok>.getOrDefault(default: Ok): Ok {
        return when (this@getOrDefault) {
            is Maybe.Some<Ok> -> value
            is Maybe.None -> default
        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any> Maybe<Ok>.getOrElse(
        @BuilderInference recover: () -> Ok,
    ): Ok {
        contract {
            callsInPlace(recover, AT_MOST_ONCE)
        }

        return when (this@getOrElse) {
            is Maybe.Some<Ok> -> value
            is Maybe.None -> recover()
        }
    }

    @NomadicDsl
    fun <Ok : Any> Maybe<Ok>.getOrNull(): Ok? {
        contract {
            returnsNotNull() implies (this@getOrNull is Maybe.Some<Ok>)
            returns(null) implies (this@getOrNull is Maybe.None)
        }

        return when (this@getOrNull) {
            is Maybe.Some<Ok> -> value
            is Maybe.None -> null
        }
    }

    @NomadicDsl
    fun <Ok : Any> Maybe<Ok>.getOrThrow(): Ok {
        contract {
            returns() implies (this@getOrThrow is Maybe.Some<Ok>)
        }

        return when (this@getOrThrow) {
            is Maybe.Some<Ok> -> value
            is Maybe.None -> throw NullPointerException("Maybe::getOrThrow threw! Got: $this")
        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any> Maybe<Ok>.getOrThrow(
        @BuilderInference throws: () -> Throwable,
    ): Ok {
        contract {
            returns() implies (this@getOrThrow is Maybe.Some<Ok>)
            callsInPlace(throws, AT_MOST_ONCE)
        }

        return when (this@getOrThrow) {
            is Maybe.Some<Ok> -> value
            is Maybe.None -> throw throws()
        }
    }

}
