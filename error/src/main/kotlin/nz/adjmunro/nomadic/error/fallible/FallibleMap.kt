package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleMap {

    @NomadicDsl
    inline fun <ErrorIn : Any, ErrorOut : Any> Fallible<ErrorIn>.map(
        @BuilderInference oops: (ErrorIn) -> ErrorOut,
    ): Fallible<ErrorOut> {
        contract {
            callsInPlace(oops, AT_MOST_ONCE)
        }

        return when (this@map) {
            is Fallible.Oops<ErrorIn> -> Fallible.Oops(oops(error))
            is Fallible.None -> this@map
        }
    }

    @NomadicDsl
    inline fun <ErrorIn : Any, ErrorOut : Any> Fallible<ErrorIn>.flatMap(
        @BuilderInference oops: (ErrorIn) -> Fallible<ErrorOut>,
    ): Fallible<ErrorOut> {
        contract {
            callsInPlace(oops, AT_MOST_ONCE)
        }

        return when (this@flatMap) {
            is Fallible.Oops<ErrorIn> -> oops(error)
            is Fallible.None -> this@flatMap
        }
    }

}
