package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.OutcomeIs.isFailure
import nz.adjmunro.nomadic.error.outcome.OutcomeIs.isSuccess
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object OutcomeOn {

    @NomadicDsl
    inline fun <Ok : Any, Error : Any> Outcome<Ok, Error>.onSuccess(
        @BuilderInference block: (Ok) -> Unit,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        if (isSuccess()) block(value)
        return this@onSuccess
    }

    @NomadicDsl
    inline fun <Ok : Any, Error : Any> Outcome<Ok, Error>.onFailure(
        @BuilderInference block: (Error) -> Unit,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        if (isFailure()) block(error)
        return this@onFailure
    }

}
