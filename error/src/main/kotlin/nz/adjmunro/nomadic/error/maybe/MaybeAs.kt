package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome
import nz.adjmunro.nomadic.error.outcome.OutcomeUtils.collapse
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import nz.adjmunro.nomadic.error.util.some
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object MaybeAs {

    @NomadicDsl
    fun Throwable.maybe(): Maybe.None {
        nonFatalOrThrow()
        return Maybe.None
    }

    @NomadicDsl
    fun <Ok : Any> Ok?.maybe(): Maybe<Ok> {
        return when (this@maybe) {
            null -> Maybe.None
            else -> Maybe.Some(value = this@maybe)
        }
    }

    @NomadicDsl
    inline infix fun <In : Any?, Out : Any> In.maybe(
        @BuilderInference transform: (In) -> Out,
    ): Maybe<Out> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@maybe) {
            null -> Maybe.None
            else -> Maybe.Some(value = transform(this@maybe))
        }
    }

    @NomadicDsl
    fun <Ok : Any> Outcome<Ok, *>.maybe(): Maybe<Ok> {
        return when (this@maybe) {
            is Outcome.Success<Ok> -> Maybe.Some(value = value)
            is Outcome.Failure -> Maybe.None
        }
    }

}
