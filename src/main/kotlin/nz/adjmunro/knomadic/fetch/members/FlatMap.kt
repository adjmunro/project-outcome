package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.inline.caller

/**
 * Transform an [initial][Fetch.Initial] fetch state to [finished][Fetch.Finished].
 *
 * - This is a convenience method for `fold`, that forces the output to be a [Fetch].
 * - Unlike [mapFetchingToFinished][Fetch.mapFetchingToFinished], this function does not re-wrap as [finished][Fetch.Finished].
 * - *Because [finished][Fetch.Finished] is not mapped, this function cannot update the type, [T] (i.e. the result of [transform] must be **of the same type** as the [Fetch] caller)!*
 *
 * @receiver The [Fetch] to transform.
 * @param T The type of the [Fetch] value.
 * @param transform The lambda to transform the [initial][Fetch.Initial] state into a [finished][Fetch.Finished].
 * @return The transformed [Fetch] of type [T].
 */
@KnomadicDsl
public inline fun <T: Any> Fetch<T>.flatMapInitial(
    transform: () -> Fetch<T>,
): Fetch<T> {
    return fold(initial = transform, fetching = ::caller, finished = ::caller)
}

/**
 * Transform a [fetching][Fetch.Fetching] fetch state to [finished][Fetch.Finished].
 *
 * - This is a convenience method for `fold`, that forces the output to be a [Fetch].
 * - Unlike [mapFetchingToFinished][Fetch.mapFetchingToFinished], this function does not re-wrap as [finished][Fetch.Finished].
 * - *Because [finished][Fetch.Finished] is not mapped, this function cannot update the type, [T] (i.e. the result of [transform] must be **of the same type** as the [Fetch] caller)!*
 *
 * @receiver The [Fetch] to transform.
 * @param T The type of the [Fetch] value.
 * @param transform The lambda to transform the [fetching][Fetch.Fetching] state into a [finished][Fetch.Finished].
 * @return The transformed [Fetch] of type [T].
 */
@KnomadicDsl
public inline fun <T: Any> Fetch<T>.flatMapFetching(
    transform: () -> Fetch<T>,
): Fetch<T> {
    return fold(initial = ::caller, fetching = transform, finished = ::caller)
}

/**
 * Transform a [finished][Fetch.Finished] fetch [result][Fetch.Finished.result].
 *
 * > This is a convenient alias for `fold`, because only the [finished][Fetch.Finished]
 * > can change the type of the [Fetch] monad. Attempting to map the other branches
 * > necessitates that the finished branch is also mapped due to type constraints.
 *
 * @receiver The [Fetch] to transform.
 * @param In The type of the [Fetch] value.
 * @param Out The type of the [transformed][transform] value.
 * @param transform The lambda to transform the [result][Fetch.Finished.result] into an [Out].
 * @return The transformed [Fetch] of type [Out].
 */
@KnomadicDsl
public inline fun <In: Any, Out: Any> Fetch<In>.flatMapFinished(
    transform: (result: In) -> Fetch<Out>,
): Fetch<Out> {
    return fold(
        initial = { Fetch.Initial },
        fetching = { Fetch.Fetching },
        finished = { transform(it) },
    )
}
