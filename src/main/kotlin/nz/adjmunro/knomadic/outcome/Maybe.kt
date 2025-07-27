@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.inline.nullfold
import nz.adjmunro.inline.throwfold
import nz.adjmunro.inline.unit
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.members.mapFailure
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.default
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.fold

@KnomadicDsl
public typealias Maybe<Ok> = Outcome<Ok, Unit>

@KnomadicDsl
public inline fun maybeFailed(ignore: Any? = null): Failure<Unit> = Failure(error = Unit)

@KnomadicDsl
public inline fun <T> T.wrapMaybe(): Maybe<T & Any> =
    throwfold(throws = ::maybeFailed) { it.nullfold(none = ::maybeFailed, some = ::Success) }

@KnomadicDsl
public inline fun <Ok : Any> Outcome<Ok, *>.asMaybe(): Maybe<Ok> = mapFailure(transform = ::unit)

@KnomadicDsl
public inline fun <Ok : Any> maybeOf(
    @BuilderInference catch: (throwable: Throwable) -> Maybe<Ok> = ::maybeFailed,
    @BuilderInference block: RaiseScope<Any>.() -> Ok,
): Maybe<Ok> {
    return RaiseScope.default<Maybe<Ok>, Any> {
        fold(
            block = block,
            catch = catch,
            recover = ::maybeFailed,
            transform = ::Success,
        )
    }
}
