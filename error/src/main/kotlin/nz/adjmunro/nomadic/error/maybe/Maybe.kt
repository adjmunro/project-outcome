package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.maybe.MaybeGet.getOrNull

sealed interface Maybe<out Ok : Any> : BinaryResult {

    @JvmInline
    value class Some<out Ok : Any>(val value: Ok) : Maybe<Ok> {
        override operator fun component1(): Ok {
            return value
        }

        override fun toString(): String {
            return "Maybe::Some<${value::class.simpleName}>($value)"
        }
    }

    data object None : Maybe<Nothing> {
        override fun toString(): String {
            return "Maybe::None"
        }
    }

    operator fun component1(): Ok? {
        return getOrNull()
    }
}
