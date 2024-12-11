package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleRecover {

    @NomadicDsl
    inline fun <Error : Any> Fallible<Error>.recover(
        @BuilderInference transform: () -> Error,
    ): Fallible<Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@recover) {
            is Fallible.None -> Fallible.Oops(transform())
            is Fallible.Oops<Error> -> this@recover
        }
    }

    @NomadicDsl
    inline fun <Error : Any> Fallible<Error>.flatRecover(
        @BuilderInference transform: () -> Fallible<Error>,
    ): Fallible<Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@flatRecover) {
            is Fallible.None -> transform()
            is Fallible.Oops<Error> -> this@flatRecover
        }
    }

}
