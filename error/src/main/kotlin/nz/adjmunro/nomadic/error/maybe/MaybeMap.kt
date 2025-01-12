package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object MaybeMap {

    @NomadicDsl
    inline infix fun <In : Any, Out : Any> Maybe<In>.map(
        @BuilderInference some: (In) -> Out,
    ): Maybe<Out> {
        contract {
            callsInPlace(some, AT_MOST_ONCE)
        }

        return when (this@map) {
            is Maybe.Some<In> -> Maybe.Some(some(value))
            is Maybe.None -> this@map
        }
    }

    @NomadicDsl
    inline infix fun <In : Any, Out : Any> Maybe<In>.flatMap(
        @BuilderInference some: (In) -> Maybe<Out>,
    ): Maybe<Out> {
        contract {
            callsInPlace(some, AT_MOST_ONCE)
        }

        return when (this@flatMap) {
            is Maybe.Some<In> -> some(value)
            is Maybe.None -> this@flatMap
        }
    }

}
