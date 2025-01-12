package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.maybe.MaybeMap.flatMap
import nz.adjmunro.nomadic.error.util.it
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object MaybeUtils {

    @NomadicDsl
    fun <Ok : Any> Maybe<Maybe<Ok>>.flatten(): Maybe<Ok> {
        return flatMap(some = ::it)
    }

    @NomadicDsl
    inline fun <Ok : Any, Out : Any?> Maybe<Ok>.collapse(
        @BuilderInference some: (Ok) -> Out,
        @BuilderInference none: () -> Out,
    ): Out {
        contract {
            callsInPlace(some, AT_MOST_ONCE)
            callsInPlace(none, AT_MOST_ONCE)
        }

        return when (this@collapse) {
            is Maybe.Some<Ok> -> some(value)
            is Maybe.None -> none()
        }
    }

}
