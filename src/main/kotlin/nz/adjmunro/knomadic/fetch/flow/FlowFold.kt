package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.fold
import nz.adjmunro.knomadic.fetch.mapFinished

/**
 * [Fold][Fetch.fold] a [FetchFlow] into a [Flow] of [Output].
 *
 * - Unlike [mapFinished][Fetch.mapFinished], `fold` places no restrictions on [Output] type.
 * - If [Output] is [Fetch], `fold` can be used to `flatMap` all fetch statuses.
 *
 * @receiver The [Fetch] to fold.
 * @param T The type of the [Fetch] value.
 * @param Output The type of the folded value.
 * @param initial The lambda to transform the [Fetch.Initial] status into an [Output].
 * @param fetching The lambda to transform the [Fetch.Fetching] status into an [Output].
 * @param finished The lambda to transform the [Fetch.Finished] status into an [Output].
 * @return The folded value of type [Output].
 */
@KnomadicDsl
public inline fun <T : Any, Output : Any?> FetchFlow<T>.fold(
    crossinline initial: suspend () -> Output,
    crossinline fetching: suspend () -> Output,
    crossinline finished: suspend (result: T) -> Output,
): Flow<Output> = map {
    it.fold(
        initial = { initial() },
        fetching = { fetching() },
        finished = { finished(it) },
    )
}
