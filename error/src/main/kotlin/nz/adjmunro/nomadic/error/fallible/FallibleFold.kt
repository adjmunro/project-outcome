package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object FallibleFold {

    @NomadicDsl
    inline fun <ErrorIn : Any, ErrorOut : Any> Fallible<ErrorIn>.flatFold(
        @BuilderInference pass: () -> Fallible<ErrorOut>,
        @BuilderInference oops: (ErrorIn) -> Fallible<ErrorOut>,
    ): Fallible<ErrorOut> {
        contract {
            callsInPlace(pass, AT_MOST_ONCE)
            callsInPlace(oops, AT_MOST_ONCE)
        }

        return when (this) {
            is Fallible.Pass -> pass()
            is Fallible.Oops -> oops(error)
        }
    }

}
