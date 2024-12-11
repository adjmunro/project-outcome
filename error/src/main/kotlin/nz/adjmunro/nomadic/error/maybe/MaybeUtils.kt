package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.maybe.MaybeMap.flatMap
import nz.adjmunro.nomadic.error.util.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object MaybeUtils {

    @NomadicDsl
    fun <Ok : Any> Maybe<Maybe<Ok>>.flatten(): Maybe<Ok> {
        return flatMap(some = ::identity)
    }

    @NomadicDsl
    inline fun <In : Any, Out : Any> Maybe<In>.collapse(
        @BuilderInference some: (In) -> Out,
        @BuilderInference none: () -> Out,
    ): Out {
        contract {
            callsInPlace(some, AT_MOST_ONCE)
            callsInPlace(none, AT_MOST_ONCE)
        }

        return when (this@collapse) {
            is Maybe.Some<In> -> some(value)
            is Maybe.None -> none()
        }
    }

}
