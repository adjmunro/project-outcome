package nz.adjmunro.nomadic.error.fallible


import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fallible.FallibleMap.flatMap
import nz.adjmunro.nomadic.error.util.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleUtils {

    @NomadicDsl
    fun <Error : Any> Fallible<Fallible<Error>>.flatten(): Fallible<Error> {
        return flatMap(oops = ::identity)
    }

    @NomadicDsl
    inline fun <ErrorIn : Any, ErrorOut : Any> Fallible<Error>.collapse(
        @BuilderInference none: () -> ErrorOut,
        @BuilderInference oops: (Error) -> ErrorOut,
    ): ErrorOut {
        contract {
            callsInPlace(none, AT_MOST_ONCE)
            callsInPlace(oops, AT_MOST_ONCE)
        }

        return when (this@collapse) {
            is Fallible.None -> none()
            is Fallible.Oops<Error> -> oops(error)
        }
    }

}
