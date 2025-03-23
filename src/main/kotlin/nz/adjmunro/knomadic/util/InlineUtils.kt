@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.util

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

@KnomadicDsl
public inline fun <T> itself(value: T): T = value

@KnomadicDsl
public inline fun <T> T.caller(ignore: Any? = null): T = this@caller

@KnomadicDsl
public inline fun rethrow(throwable: Throwable): Nothing = throw throwable

@KnomadicDsl
public inline fun nulls(ignore: Any? = null): Unit? = null

@KnomadicDsl
public inline fun <In, Out> In.nullfold(
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

@KnomadicDsl
public inline fun <In, Out> In.throwfold(
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
