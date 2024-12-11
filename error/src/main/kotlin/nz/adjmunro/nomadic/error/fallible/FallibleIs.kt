package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object FallibleIs {

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.isNone(): Boolean {
        contract {
            returns(true) implies (this@isNone is Fallible.None)
            returns(false) implies (this@isNone is Fallible.Oops<Error>)
        }

        return this@isNone is Fallible.None
    }

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.isOops(): Boolean {
        contract {
            returns(true) implies (this@isOops is Fallible.Oops<Error>)
            returns(false) implies (this@isOops is Fallible.None)
        }

        return this@isOops is Fallible.Oops<Error>
    }

    @NomadicDsl
    inline fun <Error : Any> Fallible<Error>.isNone(
        @BuilderInference predicate: () -> Boolean,
    ): Boolean {
        contract {
            returns(true) implies (this@isNone is Fallible.None)
            callsInPlace(predicate, AT_MOST_ONCE)
        }

        return this@isNone is Fallible.None && predicate()
    }

    @NomadicDsl
    inline fun <Error : Any> Fallible<Error>.isOops(
        @BuilderInference predicate: (Error) -> Boolean,
    ): Boolean {
        contract {
            returns(true) implies (this@isOops is Fallible.Oops<Error>)
            callsInPlace(predicate, AT_MOST_ONCE)
        }

        return this@isOops is Fallible.Oops<Error> && predicate(error)
    }

}
