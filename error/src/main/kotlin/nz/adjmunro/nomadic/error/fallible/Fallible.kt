package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fallible.FallibleGetError.errorOrNull
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.util.fallibleOops
import nz.adjmunro.nomadic.error.util.falliblePass
import nz.adjmunro.nomadic.error.util.rethrow
import kotlin.experimental.ExperimentalTypeInference

sealed interface Fallible<out Error : Any> : BinaryResult<Nothing, Error> {

    operator fun component1(): Error? = errorOrNull()

    data object Pass : Fallible<Nothing> {
        override fun toString(): String = "Fallible::Pass"
    }

    @JvmInline
    value class Oops<out Error : Any>(val error: Error) : Fallible<Error> {
        override operator fun component1(): Error = error
        override fun toString(): String = "Fallible::Oops<${error::class.simpleName}>($error)"
    }

    companion object {
        @OptIn(ExperimentalTypeInference::class)
        @NomadicDsl
        inline fun <Error : Any> fallibleOf(
            @BuilderInference crossinline catch: (throwable: Throwable) -> Fallible<Error> = ::rethrow,
            @BuilderInference crossinline block: RaiseScope<Error>.() -> Unit,
        ): Fallible<Error> {
            return RaiseScope.fold(
                block = block,
                catch = catch,
                recover = ::Oops,
                transform = ::falliblePass,
            )
        }
    }
}
