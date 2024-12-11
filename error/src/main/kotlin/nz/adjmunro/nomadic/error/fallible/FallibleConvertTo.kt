package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow

object FallibleConvertTo {

    @NomadicDsl
    fun <Ok : Any> Ok?.fallible(): Fallible<NullPointerException> {
        return when (this@fallible) {
            null -> Fallible.Oops(error = NullPointerException())
            else -> Fallible.None
        }
    }

    @NomadicDsl
    fun <Error : Throwable> Error.fallible(): Fallible.Oops<Error> {
        return Fallible.Oops(error = this@fallible.nonFatalOrThrow())
    }

    @NomadicDsl
    fun <Error : Any> Outcome<*, Error>.fallible(): Fallible<Error> {
        return when (this@fallible) {
            is Outcome.Success -> Fallible.None
            is Outcome.Failure<Error> -> Fallible.Oops(error)
        }
    }
}
