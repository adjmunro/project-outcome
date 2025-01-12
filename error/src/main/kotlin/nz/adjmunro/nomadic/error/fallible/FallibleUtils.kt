package nz.adjmunro.nomadic.error.fallible


import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fallible.FallibleMap.flatMap
import nz.adjmunro.nomadic.error.util.it
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleUtils {

    @NomadicDsl
    fun <Error : Any> Fallible<Fallible<Error>>.flatten(): Fallible<Error> {
        return flatMap(oops = ::it)
    }

    @NomadicDsl
    inline fun <Error : Any, Out : Any?> Fallible<Error>.collapse(
        @BuilderInference pass: () -> Out,
        @BuilderInference oops: (Error) -> Out,
    ): Out {
        contract {
            callsInPlace(pass, AT_MOST_ONCE)
            callsInPlace(oops, AT_MOST_ONCE)
        }

        return when (this@collapse) {
            is Fallible.Pass -> pass()
            is Fallible.Oops<Error> -> oops(error)
        }
    }

}
