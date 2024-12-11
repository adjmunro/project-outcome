package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.maybe.MaybeIs.isNone
import nz.adjmunro.nomadic.error.maybe.MaybeIs.isSome
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object MaybeOn {

    @NomadicDsl
    inline fun <Ok : Any> Maybe<Ok>.onSome(
        @BuilderInference block: (Ok) -> Unit,
    ): Maybe<Ok> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        if (isSome()) block(value)
        return this@onSome
    }

    @NomadicDsl
    inline fun <Ok : Any> Maybe<Ok>.onNone(
        @BuilderInference block: () -> Unit,
    ): Maybe<Ok> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        if (isNone()) block()
        return this@onNone
    }

}
