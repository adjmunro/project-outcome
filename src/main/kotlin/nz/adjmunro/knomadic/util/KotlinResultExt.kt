package nz.adjmunro.knomadic.util

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.KotlinResult
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

@KnomadicDsl
public inline fun <T> resultOf(@BuilderInference block: () -> T): KotlinResult<T> {
    contract { callsInPlace(block, AT_MOST_ONCE) }

    return try {
        KotlinResult.success(block())
    } catch (e: Throwable) {
        KotlinResult.failure(e.nonFatalOrThrow())
    }
}
