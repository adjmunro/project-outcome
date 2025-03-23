@file:Suppress("NOTHING_TO_INLINE", "UNUSED") @file:OptIn(ExperimentalTypeInference::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome.Success
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import nz.adjmunro.nomadic.error.util.nullfold
import nz.adjmunro.nomadic.error.util.rethrow
import nz.adjmunro.nomadic.error.util.throwfold
import kotlin.experimental.ExperimentalTypeInference

typealias Faulty<Error> = Outcome<Unit, Error>

@NomadicDsl
inline fun outcomePassed(ignore: Any? = null): Success<Unit> = successOf(Unit)

@NomadicDsl
inline fun <Error : Throwable> Error.wrapFaulty(): Faulty<Error> = failureOf(nonFatalOrThrow())

@NomadicDsl
inline fun <T> T.wrapFaulty(): Faulty<Throwable> =
    throwfold(::failureOf) { it.nullfold(::failureOf, ::outcomePassed) }

@NomadicDsl
inline fun <Error : Any> faultOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Faulty<Error> = ::rethrow,
    @BuilderInference crossinline block: RaiseScope<Error>.() -> Unit,
): Faulty<Error> {
    return RaiseScope.fold(
        block = block,
        catch = catch,
        recover = ::failureOf,
        transform = ::outcomePassed,
    )
}
