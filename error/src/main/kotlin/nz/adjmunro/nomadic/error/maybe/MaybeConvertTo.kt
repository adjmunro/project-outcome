package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow

object MaybeConvertTo {

    @NomadicDsl
    fun <Ok : Any> Ok?.maybe(): Maybe<Ok> {
        return when (this@maybe) {
            null -> Maybe.None
            else -> Maybe.Some(value = this@maybe)
        }
    }

    @NomadicDsl
    fun Throwable.maybe(): Maybe.None {
        nonFatalOrThrow()
        return Maybe.None
    }

    @NomadicDsl
    fun <Ok : Any> Outcome<Ok, *>.maybe(): Maybe<Ok> {
        return when (this@maybe) {
            is Outcome.Success<Ok> -> Maybe.Some(value = value)
            is Outcome.Failure -> Maybe.None
        }
    }

}
