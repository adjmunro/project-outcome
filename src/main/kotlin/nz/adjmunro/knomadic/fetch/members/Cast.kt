package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.KotlinResult
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.members.getOrNull
import nz.adjmunro.knomadic.outcome.Faulty
import nz.adjmunro.knomadic.outcome.Maybe
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.catch
import nz.adjmunro.knomadic.outcome.Failure
import nz.adjmunro.knomadic.outcome.faultyOf
import nz.adjmunro.knomadic.outcome.maybeOf
import nz.adjmunro.knomadic.util.resultOf

/**
 * Convert a [Fetch] to an [Outcome] by [catching][catch] any exceptions thrown by [getOrThrow].
 *
 * @return An [Outcome] containing the [result][nz.adjmunro.knomadic.fetch.Finished.result]
 * or [cache][nz.adjmunro.knomadic.fetch.Fetching.cache] of the fetch or a [Throwable] if an error occurred.
 */
@KnomadicDsl
public fun <Ok : Any> Fetch<Ok>.toOutcome(): Outcome<Ok, Throwable> = catch { getOrThrow() }


/**
 * Convert a [Fetch] to a [Maybe] by [catching and suppressing][maybeOf] any exceptions thrown by [getOrThrow].
 *
 * @return A [Maybe] containing the [result][nz.adjmunro.knomadic.fetch.Finished.result]
 * or [cache][nz.adjmunro.knomadic.fetch.Fetching.cache] of the fetch or a [Unit] if an error occurred.
 */
@KnomadicDsl
public fun <Ok : Any> Fetch<Ok>.toMaybe(): Maybe<Ok> = maybeOf { getOrThrow() }

/**
 * Convert a [Fetch] to a [Faulty] by [catching and wrapping][faultyOf] any exceptions thrown by [getOrThrow].
 *
 * @return A [Faulty] containing the [Unit] or a [Throwable] if an error occurred.
 */
@KnomadicDsl
public fun Fetch<*>.toFaulty(): Faulty<Throwable> = faultyOf(catch = ::Failure) { getOrThrow() }

/**
 * Convert a [Fetch] to a [KotlinResult] by catching the [resultOf] any exceptions thrown by [getOrThrow].
 *
 * @return A [KotlinResult] containing the [result][nz.adjmunro.knomadic.fetch.Finished.result]
 * or [cache][nz.adjmunro.knomadic.fetch.Fetching.cache] of the fetch or a [Throwable] if an error occurred.
 */
@KnomadicDsl
public fun <T: Any> Fetch<T>.toKotlinResult(): KotlinResult<T> = resultOf { getOrThrow() }
