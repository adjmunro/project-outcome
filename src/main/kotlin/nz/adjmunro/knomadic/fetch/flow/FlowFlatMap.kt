package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.flatMapFetching
import nz.adjmunro.knomadic.fetch.flatMapFinished
import nz.adjmunro.knomadic.fetch.flatMapInitial

/** Convenience function to apply [flatMapInitial][Fetch.flatMapInitial] inside a [Flow]. */
@KnomadicDsl
public inline fun <T : Any> FetchFlow<T>.flatMapInitial(
    crossinline transform: suspend () -> Fetch<T>,
): FetchFlow<T> {
    return map { it.flatMapInitial { transform() } }
}

/** Convenience function to apply [flatMapFetching][Fetch.flatMapFetching] inside a [Flow]. */
@KnomadicDsl
public inline fun <T : Any> FetchFlow<T>.flatMapFetching(
    crossinline transform: suspend () -> Fetch<T>,
): FetchFlow<T> {
    return map { it.flatMapFetching { transform() } }
}

/** Convenience function to apply [flatMapFinished][Fetch.flatMapFinished] inside a [Flow]. */
@KnomadicDsl
public inline fun <In : Any, Out : Any> FetchFlow<In>.flatMapFinished(
    crossinline transform: suspend (result: In) -> Fetch<Out>,
): FetchFlow<Out> {
    return map { it.flatMapFinished { transform(it) } }
}
