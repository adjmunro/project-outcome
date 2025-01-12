package nz.adjmunro.nomadic.error.fallible

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
object FallibleScope {

    @Suppress("UNCHECKED_CAST")
    @NomadicDsl
    inline infix fun <Error : Throwable> fallibleOf(block: () -> Unit): Fallible<Error> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        return try {
            block()
            Fallible.Pass
        } catch (e: Throwable) {
            Fallible.Oops(e.nonFatalOrThrow() as Error)
        }
    }

    @NomadicDsl
    inline fun <Error : Any> fallibleRaise(
        @BuilderInference catch: (throwable: Throwable) -> Fallible<Error>,
        @BuilderInference noinline block: RaiseScope<Error>.() -> Unit,
    ): Fallible<Error> {
        return block.foldEager(
            catch = catch,
            recover = { error -> Fallible.Oops(error) },
            transform = { Fallible.Pass },
        )
    }

    @NomadicDsl
    suspend fun <Error : Any> fallibleSuspend(
        @BuilderInference catch: suspend (throwable: Throwable) -> Fallible<Error>,
        @BuilderInference block: suspend RaiseScope<Error>.() -> Unit,
    ): Fallible<Error> {
        return block.foldSuspend(
            catch = catch,
            recover = { error -> Fallible.Oops(error) },
            transform = { Fallible.Pass },
        )
    }
}
