package nz.adjmunro.knomadic.raise

import kotlinx.atomicfu.atomic
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.catch
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.fold
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.raise
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.withRaiseScope
import nz.adjmunro.knomadic.raise.exception.RaiseCancellationException
import nz.adjmunro.knomadic.raise.exception.RaiseScopeLeakedException
import nz.adjmunro.knomadic.util.nonFatalOrThrow
import nz.adjmunro.knomadic.util.rethrow
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

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
public sealed interface RaiseScope<in Error : Any> {

    /**
     * Completes the scope, preventing any further errors from being raised.
     *
     * *This is to prevent the scope from being leaked.*
     */
    public fun complete()

    /**
     * Wraps [error] as a [Throwable] and throws it, short-circuiting the [RaiseScope] execution.
     *
     * @param error The error to raise, which can be any non-null type.
     * @return [Nothing] This function always throws.
     * @throws RaiseCancellationException When [RaiseScope] is active.
     * @throws RaiseScopeLeakedException If [RaiseScope.complete] has already been called.
     */
    public fun raised(error: Error): Nothing

    public class DefaultRaise<in Error : Any> @PublishedApi internal constructor() : RaiseScope<Error> {
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

    public companion object {
        @KnomadicDsl
        @PublishedApi
        internal inline fun <Ok : Any, Error : Any> withRaiseScope(
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
        public inline fun <Error : Any> RaiseScope<Error>.raise(error: () -> Error): Nothing {
            raised(error = error())
        }

        /**
         * A context runner use inside the [RaiseScope] when you wish to catch an expected
         * (non-fatal) [Throwable] and convert it to an [Error] from your domain.
         *
         * ```kotlin
         * val x: Faulty<String> = faultyOf {
         *     catch({ "$it" }) { throw IOException() } // raises "IOException"
         * }
         * ```
         *
         * @param catch A function that converts a [Throwable] to an [Error]. Re-throws by default.
         * @param block The block of code to execute.
         * @return The result of the block of code.
         * @throws Error If the block of code throws (provided [catch] maps [Throwable] to [Error]).
         * @throws Throwable if [catch] re-throws.
         */
        @KnomadicDsl
        public inline fun <Ok : Any, Error : Any> RaiseScope<Error>.catch(
            @BuilderInference catch: (throwable: Throwable) -> Error = ::rethrow,
            @BuilderInference block: RaiseScope<Error>.() -> Ok,
        ): Ok {
            contract {
                callsInPlace(catch, AT_MOST_ONCE)
                callsInPlace(block, AT_MOST_ONCE)
            }

            return try {
                block()
            } catch (e: Throwable) {
                raised(error = catch(e.nonFatalOrThrow()))
            }
        }

        @KnomadicDsl
        @Suppress("UNCHECKED_CAST")
        public inline fun <In : Any, Out : Any, Error : Any> fold(
            @BuilderInference block: (scope: RaiseScope<Error>) -> In,
            @BuilderInference catch: (throwable: Throwable) -> Out = ::rethrow,
            @BuilderInference recover: (error: Error) -> Out,
            @BuilderInference transform: (value: In) -> Out,
        ): Out {
            contract {
                // TODO: This seems to be a bug in the Kotlin compiler, it should be AT_MOST_ONCE?
                //   ...or my understanding of contracts is wrong.
                callsInPlace(block, InvocationKind.UNKNOWN)
                callsInPlace(catch, InvocationKind.UNKNOWN)
                callsInPlace(recover, InvocationKind.UNKNOWN)
                callsInPlace(transform, InvocationKind.UNKNOWN)
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
