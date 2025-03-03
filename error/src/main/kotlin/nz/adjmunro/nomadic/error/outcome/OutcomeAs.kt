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
// todo rename asOutcome?
    @NomadicDsl
    fun <Ok : Any> Ok?.asOutcome(): Outcome<Ok, Throwable> {
        return when (this@asOutcome) {
            null -> Outcome.Failure(error = NullPointerException("Outcome source was null."))
            is Throwable -> Outcome.Failure(error = this@asOutcome)
            else -> Outcome.Success(value = this@asOutcome)
        }
    }

    @NomadicDsl
    inline infix fun <In : Any?, Ok : Any, Error : Any> In.asOutcome(
        @BuilderInference transform: (In) -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract { callsInPlace(transform, AT_MOST_ONCE) }

        return transform(this@asOutcome)
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Maybe<Ok>.asOutcome(
        @BuilderInference none: () -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract { callsInPlace(none, AT_MOST_ONCE) }

        return when (this@asOutcome) {
            is Maybe.Some -> Outcome.Success(value = value)
            is Maybe.None -> none()
        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Fallible<Error>.asOutcome(
        @BuilderInference pass: () -> Outcome<Ok, Error>,
    ): Outcome<Ok, Error> {
        contract { callsInPlace(pass, AT_MOST_ONCE) }

        return when (this@asOutcome) {
            is Fallible.Pass -> pass()
            is Fallible.Oops -> Outcome.Failure(error = error)
        }
    }

}
