package nz.adjmunro.nomadic.error.raise

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object RaiseEnsure {

    @NomadicDsl
    inline fun <Error : Any> RaiseScope<Error>.ensure(
        condition: Boolean,
        @BuilderInference raise: () -> Error,
    ) {
        contract {
            callsInPlace(raise, AT_MOST_ONCE)
            returns() implies condition
        }

        return if (condition) Unit else this.raise(raise())
    }

    @NomadicDsl
    inline fun <Ok : Any, Error : Any> RaiseScope<Error>.ensureNotNull(
        value: Ok?,
        @BuilderInference raise: () -> Error,
    ): Ok {
        contract {
            callsInPlace(raise, AT_MOST_ONCE)
            returns() implies (value != null)
        }

        return value ?: this.raise(raise())
    }
}
