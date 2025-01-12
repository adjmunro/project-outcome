package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeRecover {

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.recover(
        @BuilderInference transform: (Error) -> Ok,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@recover) {
            is Outcome.Success<Ok> -> this@recover
            is Outcome.Failure<Error> -> Outcome.Success(transform(error))
        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.flatRecover(
        @BuilderInference transform: (Error) -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@flatRecover) {
            is Outcome.Success<Ok> -> this@flatRecover
            is Outcome.Failure<Error> -> transform(error)
        }
    }

}
