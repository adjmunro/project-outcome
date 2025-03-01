package nz.adjmunro.nomadic.error.raise

import kotlinx.atomicfu.atomic
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.fold
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

    /**
     * Injects the [RaiseScope] (from the current context of the interface implementation)
     * into a lambda (the receiver for this function), and invokes this function's receiver
     * with the [RaiseScope] implementation as a receiver.
     *
     * @receiver A lambda that needs a [RaiseScope] as a receiver.
     * @return The result of the lambda.
     */
//    fun <Ok : Any> (RaiseScope<Error>.() -> Ok).injectRaiseScope(): Ok {
//        return this(this@RaiseScope)
//    }
//
//    fun <Ok : Any> withRaisedScope(action: RaiseScope<Error>.() -> Ok): Ok {
//        return with(receiver = this@RaiseScope, block = action)
//    }
//
//    suspend fun <Ok : Any> withRaisedScope(action: suspend RaiseScope<Error>.() -> Ok): Ok {
//        return action(this@RaiseScope)
//    }
//
//    suspend fun <Ok : Any> (suspend RaiseScope<Error>.() -> Ok).injectRaiseScope(): Ok {
//        return this(this@RaiseScope)
//    }


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
        inline fun <Ok : Any, Error : Any> withRaiseScope(action: RaiseScope<Error>.() -> Ok): Ok {
            return with(receiver = DefaultRaise(), block = action)
        }

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
        inline fun <reified Error: Throwable> RaiseScope<Error>.expect() { /* No-Op */ }

        // todo this just called block, but then expect only informs typesafety, it doesn't map!
        inline fun <Ok, reified Error: Throwable> RaiseScope<Error>.expect(
            error: KClass<Error>,
            @BuilderInference block: RaiseScope<Error>.() -> Ok
        ): Ok {
            return try {
                block()
            } catch(e: Throwable) {
                if (e is Error) raised(error = e)
                else throw e
            }
        }

        // TODO encapsulate block's errors?
        inline fun <Ok, reified Error: Throwable> RaiseScope<Error>.expect(
            @BuilderInference block: RaiseScope<Error>.() -> Ok
        ): Ok {
            return try {
                block()
            } catch(e: Throwable) {
                raised(error = e.nonFatalOrThrow() as Error)
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

        @NomadicDsl
         suspend inline fun <In : Any, Out : Any, Error : Any> foldSuspend(
            @BuilderInference crossinline block: suspend (scope: RaiseScope<Error>) -> In,
            @BuilderInference crossinline catch: suspend (throwable: Throwable) -> Out,
            @BuilderInference crossinline recover: suspend (error: Error) -> Out,
            @BuilderInference crossinline transform: suspend (value: In) -> Out,
        ): Out {
            contract {
                callsInPlace(block, AT_MOST_ONCE)
                callsInPlace(catch, AT_MOST_ONCE)
                callsInPlace(recover, AT_MOST_ONCE)
                callsInPlace(transform, AT_MOST_ONCE)
            }

            return fold(
                block = { scope -> block(scope) },
                catch = { throwable -> catch(throwable) },
                recover = { error -> recover(error) },
                transform = { value -> transform(value) },
            )
        }
    }
}
