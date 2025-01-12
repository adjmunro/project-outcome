package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object FallibleIs {

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.isPass(): Boolean {
        contract {
            returns(true) implies (this@isPass is Fallible.Pass)
            returns(false) implies (this@isPass is Fallible.Oops<Error>)
        }

        return this@isPass is Fallible.Pass
    }

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.isOops(): Boolean {
        contract {
            returns(true) implies (this@isOops is Fallible.Oops<Error>)
            returns(false) implies (this@isOops is Fallible.Pass)
        }

        return this@isOops is Fallible.Oops<Error>
    }

    @NomadicDsl
    inline infix fun <Error : Any> Fallible<Error>.isPass(
        @BuilderInference predicate: () -> Boolean,
    ): Boolean {
        contract {
            returns(true) implies (this@isPass is Fallible.Pass)
            callsInPlace(predicate, AT_MOST_ONCE)
        }

        return this@isPass is Fallible.Pass && predicate()
    }

    @NomadicDsl
    inline infix fun <Error : Any> Fallible<Error>.isOops(
        @BuilderInference predicate: (Error) -> Boolean,
    ): Boolean {
        contract {
            returns(true) implies (this@isOops is Fallible.Oops<Error>)
            callsInPlace(predicate, AT_MOST_ONCE)
        }

        return this@isOops is Fallible.Oops<Error> && predicate(error)
    }

}
