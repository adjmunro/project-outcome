package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.mapFetchingToFinished
import nz.adjmunro.knomadic.fetch.mapFinished
import nz.adjmunro.knomadic.fetch.mapInitialToFinished
import nz.adjmunro.knomadic.fetch.mapToFinished

/** Convenience function to apply [mapFinished][Fetch.mapFinished] inside a [Flow]. */
@KnomadicDsl
public inline fun <In: Any, Out: Any> FetchFlow<In>.mapFinished(
    crossinline transform: suspend (result: In) -> Out,
): FetchFlow<Out> {
    return map { it.mapFinished { transform(it) } }
}

/** Convenience function to apply [mapToFinished][Fetch.mapToFinished] inside a [Flow]. */
@KnomadicDsl
public inline fun <T: Any> FetchFlow<T>.mapInitialToFinished(
    crossinline transform: suspend () -> T,
): FetchFlow<T> {
    return map { it.mapInitialToFinished { transform() } }
}

/** Convenience function to apply [mapFetchingToFinished][Fetch.mapFetchingToFinished] inside a [Flow]. */
@KnomadicDsl
public inline fun <T: Any> FetchFlow<T>.mapFetchingToFinished(
    crossinline transform: suspend () -> T,
): FetchFlow<T> {
    return map { it.mapFetchingToFinished { transform() } }
}

/** Convenience function to apply [mapFetchingToFinished][Fetch.mapFetchingToFinished] inside a [Flow]. */
@KnomadicDsl
public inline fun <In: Any, Out: Any> FetchFlow<In>.mapToFinished(
    crossinline initial: suspend () -> Out,
    crossinline fetching: suspend () -> Out,
    crossinline finished: suspend (result: In) -> Out,
): Flow<Fetch.Finished<Out>> {
    return map {
        it.mapToFinished(
            initial = { initial() },
            fetching = { fetching() },
            finished = { finished(it) },
        )
    }
}
