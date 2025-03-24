package nz.adjmunro.knomadic.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withTimeout
import nz.adjmunro.knomadic.FetchCollector
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.time.Duration

/**
 * Create a new [FetchFlow] with the given [block] of code to execute.
 *
 * - [Fetch.NotStarted] is for default states, and is not emitted by the resulting [FetchFlow].
 * - [Fetch.InProgress] is emitted automatically *before* [block] is executed.
 * - [Fetch.Finished] automatically encapsulates the result of [block].
 *
 * However, you can manually [emit][FlowCollector.emit] these statuses via
 * [emit][FlowCollector.emit], [reset], [fetching], and [finished].
 *
 * @param T The type of the result of the fetch.
 * @param timeout The duration to wait [withTimeout] for the fetch [block] to complete once.
 * @param recover The transformation to apply to any [non-fatal][nz.adjmunro.knomadic.util.nonFatalOrThrow] [Throwable] that is caught.
 * @param block The block of code to execute.
 * @return [FetchFlow] -- a [Flow] that encapsulates the [Fetch] behaviour.
 * @see SafeFetchFlow
 */
@KnomadicDsl
public fun <T : Any> fetch(
    timeout: Duration = Duration.INFINITE,
    @BuilderInference recover: FetchCollector<T>.(Throwable) -> Fetch<T> = { throw it },
    @BuilderInference block: suspend FetchCollector<T>.() -> T,
): FetchFlow<T> = SafeFetchFlow(
    timeout = timeout,
    recover = recover,
    block = block,
)

/**
 * A wrapper for asynchronous fetch operations.
 *
 * - [Fetch.NotStarted] is for default states, and is not generally emitted;
 * - [Fetch.InProgress] is emitted automatically *before* the fetch operation is executed when using [fetch];
 * - [Fetch.Finished] automatically encapsulates the result of the fetch operation.
 *
 * @see fetch
 * @see FetchFlow
 */
public sealed interface Fetch<out T : Any> {

    /**
     * A fetch operation that has not yet been started.
     *
     * *This is for default states, and is not generally emitted.*
     */
    public data object NotStarted : Fetch<Nothing>

    /**
     * A fetch operation that is currently in progress.
     *
     * *This is emitted automatically before the fetch operation is executed when using [fetch].*
     */
    public data object InProgress : Fetch<Nothing>

    /**
     * A fetch operation that has finished.
     *
     * *Following the single-responsibility principle, the success or failure of the [fetch] is left
     * up to the encapsulated type, [T]. It is recommended to use a [Outcome] type for this purpose.*
     *
     * @param result The result of the fetch operation.
     */
    @JvmInline
    public value class Finished<out T : Any>(public val result: T) : Fetch<T> {
        public operator fun component1(): T = result
        override fun toString(): String {
            return "Fetch.Finished<${result::class.simpleName}>(result = $result)"
        }
    }
}
