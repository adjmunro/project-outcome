package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import nz.adjmunro.nomadic.error.fallible.Fallible
import nz.adjmunro.nomadic.error.maybe.Maybe
import nz.adjmunro.nomadic.error.outcome.Outcome

/**
 * Alias for a [flow][Flow] that [fetches][Fetch] a [fallible][Fallible] result.
 *
 * ```
 * val fallible: FallibleFetch<String> = fetch {
 *    // Return a Fallible from inside the Fetch runner
 *    fallibleOf { "An error occurred." }
 * }
 * ```
 *
 * @see SafeFetchFlow.fetch
 * @see Fallible
 */
typealias FallibleFetch<Error> = Flow<Fetch<Fallible<Error>>>
typealias MaybeFetch<Ok> = Flow<Fetch<Maybe<Ok>>>
typealias OutcomeFetch<Ok, Error> = Flow<Fetch<Outcome<Ok, Error>>>
typealias ResultFetch<Ok> = Flow<Fetch<Result<Ok>>>

typealias FallibleFlow<Error> = Flow<Fallible<Error>>
typealias MaybeFlow<Ok> = Flow<Maybe<Ok>>
typealias OutcomeFlow<Ok, Error> = Flow<Outcome<Ok, Error>>
typealias ResultFlow<Ok> = Flow<Result<Ok>>
