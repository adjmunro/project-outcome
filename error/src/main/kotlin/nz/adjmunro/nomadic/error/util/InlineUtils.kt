@file:Suppress("NOTHING_TO_INLINE") @file:OptIn(
    ExperimentalContracts::class, ExperimentalTypeInference::class
)

package nz.adjmunro.nomadic.error.util

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@NomadicDsl
inline fun <T> itself(value: T): T = value

@NomadicDsl
inline fun <T> T.caller(ignore: Any? = null): T = this@caller

@NomadicDsl
inline fun rethrow(throwable: Throwable): Nothing = throw throwable

@NomadicDsl
inline fun nulls(ignore: Any? = null): Unit? = null

@NomadicDsl
inline fun <In, Out> In.nullfold(
    @BuilderInference none: (NullPointerException) -> Out,
    @BuilderInference some: (In & Any) -> Out,
): Out {
    contract {
        callsInPlace(some, AT_MOST_ONCE)
        callsInPlace(none, AT_MOST_ONCE)
    }

    return when (this@nullfold) {
        null -> none(NullPointerException("Nullfold source was null."))
        else -> some(this@nullfold)
    }
}

@NomadicDsl
inline fun <In, Out> In.throwfold(
    @BuilderInference throws: (Throwable) -> Out,
    @BuilderInference pass: (In) -> Out,
): Out {
    contract {
        callsInPlace(pass, AT_MOST_ONCE)
        callsInPlace(throws, AT_MOST_ONCE)
    }

    return when (this@throwfold) {
        is Throwable -> throws(this@throwfold)
        else -> pass(this@throwfold)
    }
}
