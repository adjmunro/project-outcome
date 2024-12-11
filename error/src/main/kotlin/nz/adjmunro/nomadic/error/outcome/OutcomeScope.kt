package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.raise.RaiseFold.foldEager
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.raise.RaiseFold.foldSuspend
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeScope {

    @Suppress("UNCHECKED_CAST")
    inline fun <Ok : Any, Error : Any> outcomeOf(
        @BuilderInference block: () -> Ok,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(block, AT_MOST_ONCE)
        }

        return try {
            Outcome.Success(block())
        } catch (e: Throwable) {
            Outcome.Failure(e.nonFatalOrThrow() as Error)
        }
    }

    @NomadicDsl
    inline fun <Ok : Any, Error : Any> outcomeRaise(
        @BuilderInference catch: (throwable: Throwable) -> Outcome<Ok, Error>,
        @BuilderInference noinline block: RaiseScope<Error>.() -> Ok,
    ): Outcome<Ok, Error> {
        return block.foldEager(
            catch = catch,
            recover = { Outcome.Failure(it) },
            transform = { Outcome.Success(it) },
        )
    }

    @NomadicDsl
    suspend fun <Ok : Any, Error : Any> outcomeSuspend(
        @BuilderInference catch: suspend (throwable: Throwable) -> Outcome<Ok, Error>,
        @BuilderInference block: suspend RaiseScope<Error>.() -> Ok,
    ): Outcome<Ok, Error> {
        return block.foldSuspend(
            catch = { catch(it) },
            recover = { Outcome.Failure(it) },
            transform = { Outcome.Success(it) },
        )
    }
}
