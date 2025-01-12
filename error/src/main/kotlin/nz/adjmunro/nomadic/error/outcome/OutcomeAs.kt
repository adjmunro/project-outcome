package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fallible.Fallible
import nz.adjmunro.nomadic.error.maybe.Maybe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeAs {

    @NomadicDsl
    fun <Ok : Any> Ok?.outcome(): Outcome<Ok, Throwable> {
        return when (this@outcome) {
            null -> Outcome.Failure(error = NullPointerException("Outcome source was null."))
            is Throwable -> Outcome.Failure(error = this@outcome)
            else -> Outcome.Success(value = this@outcome)
        }
    }

    @NomadicDsl
    inline infix fun <In : Any?, Ok : Any, Error : Any> In.outcome(
        @BuilderInference transform: (In) -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract { callsInPlace(transform, AT_MOST_ONCE) }

        return transform(this@outcome)
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Maybe<Ok>.outcome(
        @BuilderInference none: () -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract { callsInPlace(none, AT_MOST_ONCE) }

        return when (this@outcome) {
            is Maybe.Some -> Outcome.Success(value = value)
            is Maybe.None -> none()
        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Fallible<Error>.outcome(
        @BuilderInference none: () -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract { callsInPlace(none, AT_MOST_ONCE) }

        return when (this@outcome) {
            is Fallible.Pass -> none()
            is Fallible.Oops -> Outcome.Failure(error = error)
        }
    }

}
