package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.outcome.OutcomeGet.getOrNull
import nz.adjmunro.nomadic.error.outcome.OutcomeGetError.errorOrNull

sealed interface Outcome<out Ok : Any, out Error : Any> : BinaryResult {

    @JvmInline
    value class Success<out Ok : Any>(val value: Ok) : Outcome<Ok, Nothing> {
        override fun component1(): Ok {
            return value
        }

        override fun toString(): String {
            return "Outcome::Success<${value::class.simpleName}>($value)"
        }
    }

    @JvmInline
    value class Failure<out Error : Any>(val error: Error) : Outcome<Nothing, Error> {
        override fun component2(): Error {
            return error
        }

        override fun toString(): String {
            return "Outcome::Failure<${error::class.simpleName}>($error)"
        }
    }

    operator fun component1(): Ok? {
        return getOrNull()
    }

    operator fun component2(): Error? {
        return errorOrNull()
    }
}
