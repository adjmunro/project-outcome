package nz.adjmunro.nomadic.error.maybe

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.raise.RaiseFold.foldEager
import nz.adjmunro.nomadic.error.raise.RaiseFold.foldSuspend
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object MaybeScope {

    @NomadicDsl
    inline fun <Ok : Any> maybeOf(@BuilderInference block: () -> Ok): Maybe<Ok> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        return try {
            Maybe.Some(block())
        } catch (e: Throwable) {
            e.nonFatalOrThrow()
            Maybe.None
        }
    }

    @NomadicDsl
    fun <Ok : Any> maybeRaise(
        @BuilderInference block: RaiseScope<Any>.() -> Ok,
    ): Maybe<Ok> {
        return block.foldEager(
            catch = { Maybe.None },
            recover = { Maybe.None },
            transform = { value -> Maybe.Some(value) },
        )
    }

    @NomadicDsl
    suspend fun <Ok : Any> maybeSuspend(
        @BuilderInference block: suspend RaiseScope<Any>.() -> Ok,
    ): Maybe<Ok> {
        return block.foldSuspend(
            catch = { Maybe.None },
            recover = { Maybe.None },
            transform = { value -> Maybe.Some(value) },
        )
    }
}
