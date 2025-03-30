package nz.adjmunro.knomadic.raise

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.raise
import kotlin.contracts.contract

@KnomadicDsl
public inline fun <Error : Any> RaiseScope<Error>.ensure(
    condition: Boolean,
    @BuilderInference raise: () -> Error,
) {
    contract { returns() implies condition }
    return if (condition) Unit else raise(raise)
}

@KnomadicDsl
public inline fun <Ok : Any, Error : Any> RaiseScope<Error>.ensureNotNull(
    value: Ok?,
    @BuilderInference raise: () -> Error,
): Ok {
    contract { returns() implies (value != null) }
    return value ?: raise(raise)
}
