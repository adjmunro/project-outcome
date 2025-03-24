package nz.adjmunro.knomadic.fetch

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeout
import nz.adjmunro.knomadic.FetchCollector
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.util.nonFatalOrThrow
import kotlin.time.Duration

/**
 * [AbstractFlow] implementation for [FetchFlow].
 *
 * *Use [fetch] to provide an instance of this class.*
 * ```kotlin
 * // Typealias for Flow<Fetch<Int>>
 * val a: FetchFlow<Int> = fetch { 4 }
 *
 * // Will throw TimeoutCancellationException if [block] is not emitted within 4 seconds
 * val b: FetchFlow<Int> = fetch(timeout = 4.seconds) { 4 }
 *
 * // The recover param can map unexpected errors to a Fetch state.
 * val c: FetchFlow<Int> = fetch(timeout = 4.seconds, recover = { NotStarted }) { 4 }
 * ```
 * - [Fetch.NotStarted] is used for initial states, and is not emitted by the [FetchFlow].
 * - [Fetch.InProgress] is emitted automatically *before* [block] is executed.
 * - [Fetch.Finished] automatically encapsulates the result of [block].
 *
 * @property T The type of the result of the fetch.
 * @param timeout The duration to wait [withTimeout] for the fetch [block] to complete once.
 * @param recover The transformation to apply to any [non-fatal][nonFatalOrThrow] [Throwable] that is caught.
 * @param block The block of code to execute.
 * @return [FetchFlow] -- a [Flow] that encapsulates the [Fetch] behaviour.
 * @see fetch
 */
@OptIn(ExperimentalCoroutinesApi::class)
public class SafeFetchFlow<T : Any> @PublishedApi internal constructor(
    private val timeout: Duration = Duration.INFINITE,
    @BuilderInference private val recover: FetchCollector<T>.(Throwable) -> Fetch<T> = { throw it },
    @BuilderInference private val block: suspend FetchCollector<T>.() -> T,
) : AbstractFlow<Fetch<T>>(), FetchFlow<T> {
    override suspend fun collectSafely(collector: FetchCollector<T>) {
        with(collector) {
            // Automatic `fetching` status
            emit(Fetch.InProgress)

            // Execute the block & await the result
            emit(recover = this@SafeFetchFlow.recover) {
                withTimeout(timeout = this@SafeFetchFlow.timeout) {
                    Fetch.Finished(this@SafeFetchFlow.block(this@with))
                }
            }
        }
    }
}
