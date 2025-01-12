package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fallible.FallibleFold.flatFold
import nz.adjmunro.nomadic.error.util.receiver
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleDeteriorate {

    @NomadicDsl
    inline infix fun <Error : Any> Fallible<Error>.deteriorate(
        @BuilderInference transform: () -> Error,
    ): Fallible<Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return flatFold(
            pass = { Fallible.Oops(transform()) },
            oops = ::receiver,
        )
//        return when (this@deteriorate) {
//            is Fallible.Pass -> Fallible.Oops(transform())
//            is Fallible.Oops<Error> -> this@deteriorate
//        }
    }

    @NomadicDsl
    inline infix fun <Error : Any> Fallible<Error>.flatDeteriorate(
        @BuilderInference transform: () -> Fallible<Error>,
    ): Fallible<Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return flatFold(
            pass = transform,
            oops = ::receiver,
        )
//        return when (this@flatDeteriorate) {
//            is Fallible.Pass -> transform()
//            is Fallible.Oops<Error> -> this@flatDeteriorate
//        }
    }

}
