package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object MaybeIs {

    @NomadicDsl
    fun <Ok : Any> Maybe<Ok>.isSome(): Boolean {
        contract {
            returns(true) implies (this@isSome is Maybe.Some<Ok>)
            returns(false) implies (this@isSome is Maybe.None)
        }

        return this@isSome is Maybe.Some<Ok>
    }

    @NomadicDsl
    fun <Ok : Any> Maybe<Ok>.isNone(): Boolean {
        contract {
            returns(true) implies (this@isNone is Maybe.None)
            returns(false) implies (this@isNone is Maybe.Some<Ok>)
        }

        return this@isNone is Maybe.None
    }

    @NomadicDsl
    inline fun <Ok : Any> Maybe<Ok>.isSome(
        @BuilderInference predicate: (Ok) -> Boolean,
    ): Boolean {
        contract {
            returns(true) implies (this@isSome is Maybe.Some<Ok>)
            callsInPlace(predicate, AT_MOST_ONCE)
        }

        return this@isSome is Maybe.Some<Ok> && predicate(value)
    }

    @NomadicDsl
    inline fun <Ok : Any> Maybe<Ok>.isNone(
        @BuilderInference predicate: () -> Boolean,
    ): Boolean {
        contract {
            returns(true) implies (this@isNone is Maybe.None)
            callsInPlace(predicate, AT_MOST_ONCE)
        }

        return this@isNone is Maybe.None && predicate()
    }

}
