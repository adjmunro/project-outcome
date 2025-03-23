package nz.adjmunro.nomadic.error

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import nz.adjmunro.nomadic.error.outcome.Faulty
import nz.adjmunro.nomadic.error.fetch.Fetch
import nz.adjmunro.nomadic.error.outcome.Maybe
import nz.adjmunro.nomadic.error.outcome.Outcome

/**
 * Alias for Kotlin's [Result] type.
 *
 * *Consider using a [BinaryResult] instead.*
 */
@NomadicDsl
typealias KotlinResult<T> = kotlin.Result<T>

/**
 * Alias for a [Flow] of [Fetch] statuses.
 * @see Fetch.fetch
 */
@NomadicDsl
typealias FetchFlow<T> = Flow<Fetch<T>>

/**
 * Alias for a [FlowCollector] of [Fetch] statuses.
 *
 * *For internal-use only!*
 *
 * @see Fetch.fetch
 */
@NomadicDsl
internal typealias FetchCollector<T> = FlowCollector<Fetch<T>>

/**
 * Alias for a [flow][Flow] that [fetches][Fetch] a [faulty][Faulty] result.
 *
 * ```kotlin
 * val faulty: FaultyFetch<String> = fetch {
 *    // Return a Faulty from inside the Fetch runner
 *    faultyOf { "An error occurred." }
 * }
 * ```
 *
 * @see Fetch.fetch
 * @see Faulty
 */
@NomadicDsl
typealias FaultyFetch<Error> = Flow<Fetch<Faulty<Error>>>

/**
 * Alias for a [flow][Flow] that [fetches][Fetch] a [maybe][Maybe] result.
 *
 * ```kotlin
 * val maybe: MaybeFetch<String> = fetch {
 *   // Return a Maybe from inside the Fetch runner
 *   maybeOf { "A value." }
 * }
 * ```
 *
 * @see Fetch.fetch
 * @see Maybe
 */
@NomadicDsl
typealias MaybeFetch<Ok> = Flow<Fetch<Maybe<Ok>>>

/**
 * Alias for a [flow][Flow] that [fetches][Fetch] an [outcome][Outcome] result.
 *
 * ```kotlin
 * val outcome: OutcomeFetch<String, Int> = fetch {
 *   // Return an Outcome from inside the Fetch runner
 *   outcomeOf { 42 }
 * }
 * ```
 *
 * @see Fetch.fetch
 * @see Outcome
 */
@NomadicDsl
typealias OutcomeFetch<Ok, Error> = Flow<Fetch<Outcome<Ok, Error>>>

/**
 * Alias for a [flow][Flow] that [fetches][Fetch] a [result][KotlinResult].
 *
 * ```kotlin
 * val result: ResultFetch<String> = fetch {
 *   // Return a Result from inside the Fetch runner
 *   resultOf { "A value." }
 * }
 * ```
 *
 * @see Fetch.fetch
 * @see KotlinResult
 */
@NomadicDsl
typealias ResultFetch<Ok> = Flow<Fetch<KotlinResult<Ok>>>

/**
 * Alias for a [flow][Flow] of a [faulty][Faulty] result.
 *
 * ```kotlin
 * val faulty: FaultyFlow<String> = flow {
 *     // Emit a Faulty from inside the Flow
 *     emit(faultyOf { "An error occurred." })
 * }
 * ```
 *
 * @see Faulty
 */
@NomadicDsl
typealias FaultyFlow<Error> = Flow<Faulty<Error>>

/**
 * Alias for a [flow][Flow] of a [maybe][Maybe] result.
 *
 * ```kotlin
 * val maybe: MaybeFlow<String> = flow {
 *     // Emit a Maybe from inside the Flow
 *     emit(maybeOf { "A value." })
 * }
 * ```
 *
 * @see Maybe
 */
@NomadicDsl
typealias MaybeFlow<Ok> = Flow<Maybe<Ok>>

/**
 * Alias for a [flow][Flow] of an [outcome][Outcome] result.
 *
 * ```kotlin
 * val outcome: OutcomeFlow<String, Int> = flow {
 *     // Emit an Outcome from inside the Flow
 *     emit(outcomeOf { 42 })
 * }
 * ```
 *
 * @see Outcome
 */
@NomadicDsl
typealias OutcomeFlow<Ok, Error> = Flow<Outcome<Ok, Error>>

/**
 * Alias for a [flow][Flow] of a [result][KotlinResult].
 *
 * ```kotlin
 * val result: ResultFlow<String> = flow {
 *     // Emit a Result from inside the Flow
 *     emit(resultOf { "A value." })
 * }
 * ```
 *
 * @see KotlinResult
 */
@NomadicDsl
typealias ResultFlow<Ok> = Flow<KotlinResult<Ok>>
