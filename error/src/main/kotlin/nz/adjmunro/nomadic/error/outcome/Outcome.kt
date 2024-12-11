package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.BinaryResult

sealed interface Outcome<out Ok : Any, out Error : Any> : BinaryResult {

    @JvmInline
    value class Success<out Ok : Any>(val value: Ok) : Outcome<Ok, Nothing> {
        override fun toString(): String {
            return "Outcome::Success<${value::class.simpleName}>($value)"
        }
    }

    @JvmInline
    value class Failure<out Error : Any>(val error: Error) : Outcome<Nothing, Error> {
        override fun toString(): String {
            return "Outcome::Failure<${error::class.simpleName}>($error)"
        }
    }

}
