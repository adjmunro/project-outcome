@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.util.nullfold
import nz.adjmunro.knomadic.util.rethrow
import nz.adjmunro.knomadic.util.throwfold
import kotlin.experimental.ExperimentalTypeInference

@KnomadicDsl
public typealias Maybe<Ok> = Outcome<Ok, Unit>

@KnomadicDsl
public inline fun outcomeFailed(ignore: Any? = null): Outcome.Failure<Unit> = failureOf(Unit)

@KnomadicDsl
public inline fun <T> T.wrapMaybe(): Maybe<T & Any> =
    throwfold(::outcomeFailed) { it.nullfold(::outcomeFailed, ::successOf) }

@KnomadicDsl
public suspend inline fun <Ok : Any> maybeOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Maybe<Ok> = ::rethrow,
    @BuilderInference crossinline block: suspend RaiseScope<Any>.() -> Ok,
): Maybe<Ok> {
    return RaiseScope.fold(
        block = block,
        catch = catch,
        recover = ::outcomeFailed,
        transform = ::successOf,
    )
}
