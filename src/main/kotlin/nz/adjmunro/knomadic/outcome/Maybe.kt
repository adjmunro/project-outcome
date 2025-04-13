@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.default
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.fold
import nz.adjmunro.knomadic.util.nullfold
import nz.adjmunro.knomadic.util.rethrow
import nz.adjmunro.knomadic.util.throwfold

@KnomadicDsl
public typealias Maybe<Ok> = Outcome<Ok, Unit>

@KnomadicDsl
public inline fun outcomeFailed(ignore: Any? = null): Outcome.Failure<Unit> = failureOf(Unit)

@KnomadicDsl
public inline fun <T> T.wrapMaybe(): Maybe<T & Any> =
    throwfold(::outcomeFailed) { it.nullfold(::outcomeFailed, ::successOf) }

@KnomadicDsl
public inline fun <Ok : Any> maybeOf(
    @BuilderInference catch: (throwable: Throwable) -> Maybe<Ok> = ::rethrow,
    @BuilderInference block: RaiseScope<Any>.() -> Ok,
): Maybe<Ok> {
    return RaiseScope.default<Maybe<Ok>, Any> {
        fold(
            block = block,
            catch = catch,
            recover = ::outcomeFailed,
            transform = ::successOf,
        )
    }
}
