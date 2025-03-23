@file:Suppress("NOTHING_TO_INLINE", "UNUSED") @file:OptIn(ExperimentalTypeInference::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome.Failure
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.util.nullfold
import nz.adjmunro.nomadic.error.util.rethrow
import nz.adjmunro.nomadic.error.util.throwfold
import kotlin.experimental.ExperimentalTypeInference

typealias Maybe<Ok> = Outcome<Ok, Unit>

@NomadicDsl
inline fun outcomeFailed(ignore: Any? = null): Failure<Unit> = failureOf(Unit)

@NomadicDsl
inline fun <T> T.wrapMaybe(): Maybe<T & Any> =
    throwfold(::outcomeFailed) { it.nullfold(::outcomeFailed, ::successOf) }

@NomadicDsl
inline fun <Ok : Any> maybeOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Maybe<Ok> = ::rethrow,
    @BuilderInference crossinline block: RaiseScope<Any>.() -> Ok,
): Maybe<Ok> {
    return RaiseScope.fold(
        block = block,
        catch = catch,
        recover = ::outcomeFailed,
        transform = ::successOf,
    )
}
