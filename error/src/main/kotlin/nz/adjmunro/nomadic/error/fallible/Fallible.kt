package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.fallible.FallibleGetError.errorOrNull

sealed interface Fallible<out Error : Any> : BinaryResult<Nothing, Error> {

    data object Pass : Fallible<Nothing> {
        override fun toString(): String {
            return "Fallible::Pass"
        }
    }

    @JvmInline
    value class Oops<out Error : Any>(val error: Error) : Fallible<Error> {
        override operator fun component1(): Error {
            return error
        }

        override fun toString(): String {
            return "Fallible::Oops<${error::class.simpleName}>($error)"
        }
    }

    operator fun component1(): Error? {
        return errorOrNull()
    }

}
