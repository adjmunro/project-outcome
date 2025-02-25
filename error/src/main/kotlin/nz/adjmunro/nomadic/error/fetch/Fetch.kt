package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withTimeout
import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.FetchCollector
import nz.adjmunro.nomadic.error.FetchFlow
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fetch.Fetch.Companion.fetch
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.completed
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.fetching
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.reset
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import kotlin.experimental.ExperimentalTypeInference
import kotlin.time.Duration

/**
 * A wrapper for asynchronous fetch operations.
 *
 * - [Fetch.NotStarted] is for default states, and is not generally emitted;
 * - [Fetch.InProgress] is emitted automatically *before* the fetch operation is executed when using [fetch];
 * - [Fetch.Completed] automatically encapsulates the result of the fetch operation.
 *
 * @see Fetch.fetch
 * @see FetchFlow
 */
sealed interface Fetch<out T : Any> {

    /**
     * A fetch operation that has not yet been started.
     *
     * *This is for default states, and is not generally emitted.*
     */
    data object NotStarted : Fetch<Nothing>

    /**
     * A fetch operation that is currently in progress.
     *
     * *This is emitted automatically before the fetch operation is executed when using [fetch].*
     */
    data object InProgress : Fetch<Nothing>

    /**
     * A fetch operation that has completed.
     *
     * *Following the single-responsibility principle, the success or failure of the [fetch] is left
     * up to the encapsulated type, [T]. It is recommended to use a [BinaryResult] type for this purpose.*
     *
     * @param result The result of the fetch operation.
     */
    @JvmInline
    value class Completed<out T : Any>(val result: T) : Fetch<T> {
        operator fun component1(): T = result
        override fun toString(): String {
            return "Fetch.Completed<${result::class.simpleName}>(result = $result)"
        }
    }

    companion object {
        /**
         * Create a new [FetchFlow] with the given [block] of code to execute.
         *
         * - [Fetch.NotStarted] is for default states, and is not emitted by the resulting [FetchFlow].
         * - [Fetch.InProgress] is emitted automatically *before* [block] is executed.
         * - [Fetch.Completed] automatically encapsulates the result of [block].
         *
         * However, you can manually [emit][FlowCollector.emit] these statuses via
         * [emit][FlowCollector.emit], [reset], [fetching], and [completed].
         *
         * @param T The type of the result of the fetch.
         * @param timeout The duration to wait [withTimeout] for the fetch [block] to complete once.
         * @param recover The transformation to apply to any [non-fatal][nonFatalOrThrow] [Throwable] that is caught.
         * @param block The block of code to execute.
         * @return [FetchFlow] -- a [Flow] that encapsulates the [Fetch] behaviour.
         * @see SafeFetchFlow
         */
        @NomadicDsl
        @OptIn(ExperimentalTypeInference::class)
        fun <T : Any> fetch(
            timeout: Duration = Duration.INFINITE,
            @BuilderInference recover: FetchCollector<T>.(Throwable) -> Fetch<T> = { throw it },
            @BuilderInference block: suspend FetchCollector<T>.() -> T,
        ): FetchFlow<T> = SafeFetchFlow(
            timeout = timeout,
            recover = recover,
            block = block,
        )
    }
}
