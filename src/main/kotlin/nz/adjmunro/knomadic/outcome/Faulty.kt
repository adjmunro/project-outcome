@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.util.nonFatalOrThrow
import nz.adjmunro.knomadic.util.nullfold
import nz.adjmunro.knomadic.util.rethrow
import nz.adjmunro.knomadic.util.throwfold

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
public suspend inline fun <Error : Any> faultyOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Faulty<Error> = ::rethrow,
    @BuilderInference crossinline block: suspend RaiseScope<Error>.() -> Unit,
): Faulty<Error> {
    return RaiseScope.fold(
        block = block,
        catch = catch,
        recover = ::failureOf,
        transform = ::outcomePassed,
    )
}
