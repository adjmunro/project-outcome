package nz.adjmunro.nomadic.error.raise

import kotlinx.atomicfu.atomic
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.fold
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.raise
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.withRaiseScope
import nz.adjmunro.nomadic.error.raise.exception.RaiseCancellationException
import nz.adjmunro.nomadic.error.raise.exception.RaiseScopeLeakedException
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import nz.adjmunro.nomadic.error.util.rethrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass

/**
 * A scope for raising errors.
 *
 * Use [withRaiseScope] context runner to create a new [RaiseScope]. Within the scope,
 * the [raise] method can be called to throw any [Error] as if it were a [Throwable] and
 * in doing so, short-circuit the scope execution.
 *
 * @property Error The type of error that can be raised.
 *
 * @see fold
 */
sealed interface RaiseScope<in Error : Any> {

    /**
     * Completes the scope, preventing any further errors from being raised.
     *
     * *This is to prevent the scope from being leaked.*
     */
    fun complete()

    /**
     * Wraps [error] as a [Throwable] and throws it, short-circuiting the [RaiseScope] execution.
     *
     * @param error The error to raise, which can be any non-null type.
     * @return [Nothing] This function always throws.
     * @throws RaiseCancellationException When [RaiseScope] is active.
     * @throws RaiseScopeLeakedException If [RaiseScope.complete] has already been called.
     */
    fun raised(error: Error): Nothing

    class DefaultRaise<in Error : Any> @PublishedApi internal constructor() : RaiseScope<Error> {
        private val active = atomic(initial = true)

        override fun complete() {
            active.getAndSet(value = false)
        }

        override fun raised(error: Error): Nothing {
            when (active.value) {
                true -> throw RaiseCancellationException(error)
                false -> throw RaiseScopeLeakedException()
            }
        }
    }

    @OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
    companion object {
        @NomadicDsl
        inline fun <Ok : Any, Error : Any> withRaiseScope(
            @BuilderInference action: RaiseScope<Error>.() -> Ok,
        ): Ok = with(receiver = DefaultRaise(), block = action)

        /**
         * Invokes [error], wraps it as a [Throwable] and then throws it,
         * short-circuiting the [RaiseScope] execution.
         *
         * @param error The error to raise, which can be any non-null type.
         * @return [Nothing] This function always throws.
         * @throws RaiseCancellationException When [RaiseScope] is active.
         * @throws RaiseScopeLeakedException If [RaiseScope.complete] has already been called.
         * @see RaiseScope.raise
         */
        inline fun <Error : Any> RaiseScope<Error>.raise(error: () -> Error): Nothing {
            raised(error = error())
        }

        @Suppress("UnusedReceiverParameter")
        inline fun <reified Error : Throwable> RaiseScope<Error>.expect() { /* No-Op */
        }

        // todo this just called block, but then expect only informs typesafety, it doesn't map!
        inline fun <Ok, reified Error : Throwable> RaiseScope<Error>.expect(
            error: KClass<Error>,
            @BuilderInference block: RaiseScope<Error>.() -> Ok,
        ): Ok {
            return try {
                block()
            } catch (e: Throwable) {
                if (e is Error) raised(error = e)
                else throw e
            }
        }

        // TODO encapsulate block's errors?
        inline fun <Ok, reified Error : Throwable> RaiseScope<Error>.expect(
            @BuilderInference block: RaiseScope<Error>.() -> Ok,
        ): Ok {
            return try {
                block()
            } catch (e: Throwable) {
                raised(error = e.nonFatalOrThrow() as Error)
            }
        }

        inline fun <Ok : Any, Error : Any, T : Throwable> RaiseScope<Error>.catch(
            @BuilderInference catch: (throwable: T) -> Error = { it as Error },
            @BuilderInference block: RaiseScope<Error>.() -> Ok,
        ): Ok {
            return try {
                block()
            } catch (e: Throwable) {
                raised(error = catch(e.nonFatalOrThrow() as T))
            }
        }

        @NomadicDsl
        @Suppress("UNCHECKED_CAST")
        inline fun <In : Any, Out : Any, Error : Any> fold(
            @BuilderInference block: (scope: RaiseScope<Error>) -> In,
            @BuilderInference catch: (throwable: Throwable) -> Out = ::rethrow,
            @BuilderInference recover: (error: Error) -> Out,
            @BuilderInference transform: (value: In) -> Out,
        ): Out {
            contract {
                callsInPlace(block, AT_MOST_ONCE)
                callsInPlace(catch, AT_MOST_ONCE)
                callsInPlace(recover, AT_MOST_ONCE)
                callsInPlace(transform, AT_MOST_ONCE)
            }

            return withRaiseScope {
                try {
                    val result = block(this@withRaiseScope)
                    complete()
                    transform(result)
                } catch (e: RaiseCancellationException) {
                    complete()
                    recover(e.error as Error)
                } catch (e: Throwable) {
                    complete()
                    catch(e.nonFatalOrThrow())
                }
            }
        }
    }
}
