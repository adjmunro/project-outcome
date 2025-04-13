package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.getOrDefault
import nz.adjmunro.knomadic.fetch.getOrElse
import nz.adjmunro.knomadic.fetch.getOrNull
import nz.adjmunro.knomadic.fetch.getOrThrow
import nz.adjmunro.knomadic.fetch.unwrap

/**
 * @return A [Flow] of the [result][Fetch.Finished.result] of a [Fetch] or the [default] value.
 */
@KnomadicDsl
public infix fun <T: Any> FetchFlow<T>.fetchOrDefault(default: T): Flow<T> {
    return map { it.getOrDefault(default) }
}

/**
 * @return A [Flow] of the [result][Fetch.Finished.result] of a [Fetch] or the result of [recover].
 */
@KnomadicDsl
public inline fun <T: Any> FetchFlow<T>.fetchOrElse(
    crossinline recover: suspend (Fetch<T>) -> T
): Flow<T> {
    return map { it.getOrElse { recover(it) } }
}

/**
 * @return A [Flow] of the [result][Fetch.Finished.result] of a [Fetch] or `null`.
 */
@KnomadicDsl
public fun <T: Any> FetchFlow<T>.fetchOrNull(): Flow<T?> {
    return map { it.getOrNull() }
}

/**
 * @return A [Flow] of the [result][Fetch.Finished.result] of a [Fetch] or `throws`.
 * @throws IllegalStateException if the [Fetch] is a [Fetch.Initial] or [Fetch.Fetching].
 */
@KnomadicDsl
public fun <T: Any> FetchFlow<T>.fetchOrThrow(): Flow<T> {
    return map { it.getOrThrow() }
}

/**
 * Attempt to unwrap a [FetchFlow] to produce a [Flow] of it's [result][Fetch.Finished.result].
 *
 * ```kotlin
 * val fetch: Fetch<String> = Fetch.Initial
 * fetch.unwrap()           // getOrThrow() (default behaviour)
 * fetch.unwrap { null }    // getOrNull() (initial & fetching to null)
 * fetch.unwrap { "$it" }   // getOrElse() (initial & fetching to string)
 * ```
 *
 * @return A [Flow] of the [result][Fetch.Finished.result] of a [Fetch] or the result of [recover].
 * @throws IllegalStateException if default [recover] value is used and fetch is [initial][Fetch.Initial] or [in progress][Fetch.Fetching].
 * @see FetchFlow.fetchOrThrow
 * @see FetchFlow.fetchOrNull
 * @see FetchFlow.fetchOrElse
 * @see FetchFlow.fetchOrDefault
 */
@KnomadicDsl
public inline fun <T> FetchFlow<T & Any>.fetchUnwrap(
    crossinline recover: suspend (Fetch<T & Any>) -> T = { error("Fetch has not finished!") },
): Flow<T> {
    return map { it.unwrap() }
}
