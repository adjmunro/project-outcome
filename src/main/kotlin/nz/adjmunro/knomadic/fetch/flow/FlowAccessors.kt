package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.members.getOrDefault
import nz.adjmunro.knomadic.fetch.members.getOrElse
import nz.adjmunro.knomadic.fetch.members.getOrNull
import nz.adjmunro.knomadic.fetch.members.getOrThrow

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
