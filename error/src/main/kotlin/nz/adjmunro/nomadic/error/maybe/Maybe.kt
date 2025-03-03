package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.maybe.MaybeGet.getOrNull
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.util.maybeNone
import nz.adjmunro.nomadic.error.util.maybeSome
import kotlin.experimental.ExperimentalTypeInference

sealed interface Maybe<out Ok : Any> : BinaryResult<Ok, Nothing> {

    operator fun component1(): Ok? = getOrNull()

    @JvmInline
    value class Some<out Ok : Any>(val value: Ok) : Maybe<Ok> {
        override operator fun component1(): Ok = value
        override fun toString(): String = "Maybe::Some<${value::class.simpleName}>($value)"
    }

    data object None : Maybe<Nothing> {
        override fun toString(): String = "Maybe::None"
    }

    companion object {
        @OptIn(ExperimentalTypeInference::class)
        @NomadicDsl
        inline fun <Ok : Any> maybeOf(
            @BuilderInference crossinline block: RaiseScope<Any>.() -> Ok,
        ): Maybe<Ok> {
            return RaiseScope.fold(
                block = block,
                catch = ::maybeNone,
                recover = ::maybeNone,
                transform = ::Some,
            )
        }
    }
}
