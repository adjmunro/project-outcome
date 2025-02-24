package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.internal.NopCollector.emit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fetch.Fetch.Completed
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import nz.adjmunro.nomadic.error.fetch.Fetch.TimedOut
import nz.adjmunro.nomadic.error.util.FlowTransformExt.onEachInstance
import kotlin.experimental.ExperimentalTypeInference
import kotlin.time.Duration

/**
 * Alias for a [Flow] of [Fetch] statuses.
 * @see SafeFetchFlow.fetch
 */
typealias FetchFlow<T> = Flow<Fetch<T>>

/**
 * Alias for a [FlowCollector] of [Fetch] statuses.
 * @see SafeFetchFlow.fetch
 */
typealias FetchCollector<T> = FlowCollector<Fetch<T>>

//interface FetchFlowI<out T : Any> : Flow<Fetch<T>> {
//
//    companion object {
//        @NomadicDsl
//        @OptIn(ExperimentalTypeInference::class)
//        fun <T : Any> fetch(
//            @BuilderInference block: suspend FetchFlowCollector<T>.() -> T,
//        ): Flow<Fetch<T>> = SafeFetchFlow(block)
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private class SafeFetchFlow<T : Any>(
//        private val block: suspend FetchFlowCollector<T>.() -> T,
//    ) : AbstractFlow<Fetch<T>>(), FetchFlow<T> {
//
//        override suspend fun collectSafely(collector: FlowCollector<Fetch<T>>) {
//            // Re-route to the `collectFetch` method
//            collectFetch(fetchCollector = FetchFlowCollector(collector = collector))
//        }
//
//        private suspend fun collectFetch(fetchCollector: FetchFlowCollector<T>) {
//            with(fetchCollector) {
//                // Automatic `fetching` status
//                fetching()
//
//                // Execute the block & await the result
//                completed(block())
//            }
//        }
//    }
//}


@OptIn(ExperimentalCoroutinesApi::class)
class SafeFetchFlow<T : Any> @PublishedApi internal constructor(
    private val timeout: Duration = Duration.INFINITE,
    private val block: suspend FetchCollector<T>.() -> T,
) : AbstractFlow<Fetch<T>>() {
    override suspend fun collectSafely(collector: FetchCollector<T>) {
        with(collector) {
            // Automatic `fetching` status
            emit(InProgress)

            // Execute the block & await the result
            emit {
                withTimeoutOrNull(timeout = timeout) { Completed(block()) }
                    ?: TimedOut
            }
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
         */
        @NomadicDsl
        @OptIn(ExperimentalTypeInference::class)
        fun <T : Any> fetch(
            timeout: Duration = Duration.INFINITE,
            @BuilderInference block: suspend FetchCollector<T>.() -> T,
        ): FetchFlow<T> = SafeFetchFlow(timeout = timeout, block = block)

        /**
         * [Send][FlowCollector.emit] a [fetch not started][Fetch.NotStarted]
         * status to the current [flow-scope][Flow].
         */
        @NomadicDsl
        suspend inline fun FetchCollector<Nothing>.reset() {
            emit(NotStarted)
        }

        /**
         * [Send][FlowCollector.emit] a [fetch in progress][Fetch.InProgress]
         * status to the current [flow-scope][Flow].
         */
        @NomadicDsl
        suspend inline fun FetchCollector<Nothing>.fetching() {
            emit(InProgress)
        }

        /**
         * [Send][FlowCollector.emit] a [fetch completed][Fetch.Completed]
         * status to the current [flow-scope][Flow], with the encapsulated [result].
         */
        @NomadicDsl
        suspend inline fun <T : Any> FetchCollector<T>.completed(result: T) {
            emit(Completed(result = result))
        }

        @NomadicDsl
        suspend inline fun <T> FlowCollector<T>.emit(block: FlowCollector<T>.() -> T) {
            emit(block())
        }

        @OptIn(FlowPreview::class)
        inline fun <T> Flow<T>.recoverTimeout(
            duration: Duration,
            crossinline recover: suspend (TimeoutCancellationException) -> T,
        ): Flow<T> {
            return timeout(timeout = duration).catch { e: Throwable ->
                if (e is TimeoutCancellationException) emit(recover(e))
                else throw e
            }
        }

        inline fun <T: Any> FetchFlow<T>.recover(
            crossinline recover: suspend Fetch.TimedOut.() -> Fetch<T>,
        ): FetchFlow<T> {
            return map { if(it is Fetch.TimedOut) recover(it) else it }
        }
    }
}
