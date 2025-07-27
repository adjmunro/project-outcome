@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.members.mapSuccess
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.default
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.fold
import nz.adjmunro.knomadic.util.nonFatalOrThrow
import nz.adjmunro.inline.nullfold
import nz.adjmunro.inline.rethrow
import nz.adjmunro.inline.throwfold
import nz.adjmunro.inline.unit

@KnomadicDsl
public typealias Faulty<Error> = Outcome<Unit, Error>

@KnomadicDsl
public inline fun outcomePassed(ignore: Any? = null): Outcome.Success<Unit> = successOf(Unit)

@KnomadicDsl
public inline fun <Error : Throwable> Error.wrapFaulty(): Faulty<Error> = failureOf(nonFatalOrThrow())

@KnomadicDsl
public inline fun <T> T.wrapFaulty(): Faulty<Throwable> =
    throwfold(::failureOf) { it.nullfold(::failureOf, ::outcomePassed) }

@KnomadicDsl
public inline fun <Error : Any> Outcome<*, Error>.asFaulty(): Faulty<Error> = mapSuccess(::unit)

@KnomadicDsl
public inline fun <Error : Any> faultyOf(
    @BuilderInference catch: (throwable: Throwable) -> Faulty<Error> = ::rethrow,
    @BuilderInference block: RaiseScope<Error>.() -> Unit,
): Faulty<Error> {
    return RaiseScope.default<Faulty<Error>, Error> {
        fold(
            block = block,
            catch = catch,
            recover = ::failureOf,
            transform = ::outcomePassed,
        )
    }
}
