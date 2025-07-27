@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package nz.adjmunro.knomadic.outcome

import nz.adjmunro.inline.nullfold
import nz.adjmunro.inline.rethrow
import nz.adjmunro.inline.throwfold
import nz.adjmunro.inline.unit
import nz.adjmunro.knomadic.outcome.members.mapSuccess
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.default
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.fold
import nz.adjmunro.knomadic.util.nonFatalOrThrow



@OutcomeDsl
public inline fun faultySuccess(ignore: Any? = null): Success<Unit> = Success(Unit)

@OutcomeDsl
public inline fun <Error : Throwable> Error.wrapFaulty(): Faulty<Error> = Failure(nonFatalOrThrow())

@OutcomeDsl
public inline fun <T> T.wrapFaulty(): Faulty<Throwable> =
    throwfold(throws = ::Failure) { it.nullfold(none = ::Failure, some = ::faultySuccess) }

@OutcomeDsl
public inline fun <Error : Any> Outcome<*, Error>.asFaulty(): Faulty<Error> = mapSuccess(::unit)

@OutcomeDsl
public inline fun <Error : Any> faultyOf(
    @BuilderInference catch: (throwable: Throwable) -> Faulty<Error> = ::rethrow,
    @BuilderInference block: RaiseScope<Error>.() -> Unit,
): Faulty<Error> {
    return RaiseScope.default<Faulty<Error>, Error> {
        fold(
            block = block,
            catch = catch,
            recover = ::Failure,
            transform = ::faultySuccess,
        )
    }
}
