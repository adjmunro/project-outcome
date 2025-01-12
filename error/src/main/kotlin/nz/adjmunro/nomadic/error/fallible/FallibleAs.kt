package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleAs {

    @NomadicDsl
    fun <Error : Throwable> Error.fallible(): Fallible.Oops<Error> {
        return Fallible.Oops(error = this@fallible.nonFatalOrThrow())
    }

    @NomadicDsl
    fun <Ok : Any> Ok?.fallible(): Fallible<NullPointerException> {
        return when (this@fallible) {
            null -> Fallible.Oops(error = NullPointerException("Fallible source was null."))
            else -> Fallible.Pass
        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Ok?.fallible(
        @BuilderInference transform: () -> Error,
    ): Fallible<Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@fallible) {
            null -> Fallible.Oops(error = transform())
            else -> Fallible.Pass
        }
    }

    @NomadicDsl
    fun <Error : Any> Outcome<*, Error>.fallible(): Fallible<Error> {
        return when (this@fallible) {
            is Outcome.Success -> Fallible.Pass
            is Outcome.Failure<Error> -> Fallible.Oops(error)
        }
    }
}
