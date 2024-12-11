package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object FallibleGetError {

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.errorOrDefault(default: Error): Error {
        return when (this@errorOrDefault) {
            is Fallible.None -> default
            is Fallible.Oops<Error> -> error
        }
    }

    @NomadicDsl
    inline fun <Error : Any> Fallible<Error>.errorOrElse(
        @BuilderInference transform: () -> Error,
    ): Error {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return when (this@errorOrElse) {
            is Fallible.None -> transform()
            is Fallible.Oops<Error> -> error
        }
    }

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.errorOrNull(): Error? {
        contract {
            returnsNotNull() implies (this@errorOrNull is Fallible.Oops<Error>)
            returns(null) implies (this@errorOrNull is Fallible.None)
        }

        return when (this@errorOrNull) {
            is Fallible.None -> null
            is Fallible.Oops<Error> -> error
        }
    }

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.errorOrThrow(): Error {
        contract {
            returns() implies (this@errorOrThrow is Fallible.Oops<Error>)
        }

        return when (this@errorOrThrow) {
            is Fallible.None -> error("Fallible::errorOrThrow threw! Got: $this")
            is Fallible.Oops<Error> -> error
        }
    }

    @NomadicDsl
    fun <Error : Any> Fallible<Error>.errorOrThrow(
        @BuilderInference throws: () -> Throwable,
    ): Error {
        contract {
            returns() implies (this@errorOrThrow is Fallible.Oops<Error>)
            callsInPlace(throws, AT_MOST_ONCE)
        }

        return when (this@errorOrThrow) {
            is Fallible.None -> throw throws()
            is Fallible.Oops<Error> -> error
        }
    }

}
