package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.BinaryResult

sealed interface Fallible<out Error : Any> : BinaryResult {

    data object None : Fallible<Nothing> {
        override fun toString(): String {
            return "Fallible::None"
        }
    }

    @JvmInline
    value class Oops<out Error : Any>(val error: Error) : Fallible<Error> {
        override fun toString(): String {
            return "Fallible::Oops<${error::class.simpleName}>($error)"
        }
    }

}
