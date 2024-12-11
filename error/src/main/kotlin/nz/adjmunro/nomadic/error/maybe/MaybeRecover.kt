package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object MaybeRecover {

    @NomadicDsl
    inline fun <Ok : Any> Maybe<Ok>.recover(
        @BuilderInference transform: () -> Ok,
    ): Maybe<Ok> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@recover) {
            is Maybe.Some<Ok> -> Maybe.Some(transform())
            is Maybe.None -> this@recover
        }
    }

    @NomadicDsl
    inline fun <Ok : Any> Maybe<Ok>.flatRecover(
        @BuilderInference transform: () -> Maybe<Ok>,
    ): Maybe<Ok> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@flatRecover) {
            is Maybe.Some<Ok> -> transform()
            is Maybe.None -> this@flatRecover
        }
    }

}
