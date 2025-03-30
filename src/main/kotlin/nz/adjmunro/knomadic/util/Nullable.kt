package nz.adjmunro.knomadic.util

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

/** Runner that converts any [non-fatal][nonFatalOrThrow] exception into `null`. */
@KnomadicDsl
public inline fun <T> nullable(@BuilderInference block: () -> T): T? {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    return resultOf(block).getOrNull()
}
