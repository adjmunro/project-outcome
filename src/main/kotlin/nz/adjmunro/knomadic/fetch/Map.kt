package nz.adjmunro.knomadic.fetch

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.util.caller

/**
 * Transform a [finished][Fetch.Finished] fetch [result][Fetch.Finished.result].
 *
 * - Unlike [fold][Fetch.fold], `mapFinished` re-wraps the results of [transform] as [finished][Fetch.Finished].
 * - This is a convenient alias for `fold`, because usually only the [finished][Fetch.Finished] branch is interesting.
 *
 * @receiver The [Fetch] to transform.
 * @param In The type of the [Fetch] value.
 * @param Out The type of the [transformed][transform] value.
 * @param transform The lambda to transform the [result][Fetch.Finished.result] into an [Out].
 * @return The transformed [Fetch] of type [Out].
 */
@KnomadicDsl
public inline fun <In: Any, Out: Any> Fetch<In>.mapFinished(
    transform: (result: In) -> Out,
): Fetch<Out> {
    return fold(
        initial = { Fetch.Initial },
        fetching = { Fetch.Fetching },
        finished = { Fetch.Finished(transform(it)) },
    )
}

/**
 * Transform an [initial][Fetch.Initial] fetch state to [finished][Fetch.Finished].
 * 
 * > *Because [finished][Fetch.Finished] is not mapped, this function cannot update the type, [T] 
 * > (i.e. the result of [transform] must be **of the same type** as the [Fetch] caller)!*
 * 
 * @receiver The [Fetch] to transform.
 * @param T The type of the [Fetch] value.
 * @param transform The lambda to transform the [initial][Fetch.Initial] state into a [finished][Fetch.Finished].
 * @return The transformed [Fetch] of type [T].
 */
@KnomadicDsl
public inline fun <T: Any> Fetch<T>.mapInitialToFinished(transform: () -> T): Fetch<T> {
    return fold(initial = { Fetch.Finished(transform()) }, fetching = ::caller, finished = ::caller)
}

/**
 * Transform a [fetching][Fetch.Fetching] fetch state to [finished][Fetch.Finished].
 * 
 * > *Because [finished][Fetch.Finished] is not mapped, this function cannot update the type, [T]
 * > (i.e. the result of [transform] must be **of the same type** as the [Fetch] caller)!*
 * 
 * @receiver The [Fetch] to transform.
 * @param T The type of the [Fetch] value.
 * @param transform The lambda to transform the [fetching][Fetch.Fetching] state into a [finished][Fetch.Finished].
 * @return The transformed [Fetch] of type [T].
 */
@KnomadicDsl
public inline fun <T: Any> Fetch<T>.mapFetchingToFinished(transform: () -> T): Fetch<T> {
    return fold(initial = ::caller, fetching = { Fetch.Finished(transform()) }, finished = ::caller)
}

/**
 * Transform all [Fetch] states into a [finished][Fetch.Finished] state.
 *
 * > Unlike [fold][Fetch.fold], `mapToFinished` re-wraps the results of each lambda as
 * > a [finished][Fetch.Finished] state of type [Out].
 *
 * @receiver The [Fetch] to transform.
 * @param In The type of the [Fetch] value.
 * @param Out The type of the transformed values.
 * @param initial The lambda to transform the [initial][Fetch.Initial] state into a [finished][Fetch.Finished].
 * @param fetching The lambda to transform the [fetching][Fetch.Fetching] state into a [finished][Fetch.Finished].
 * @param finished The lambda to transform the [finished][Fetch.Finished] state into a [finished][Fetch.Finished].
 * @return The transformed [Fetch] of type [Out].
 */
@KnomadicDsl
public inline fun <In: Any, Out: Any> Fetch<In>.mapToFinished(
    initial: () -> Out,
    fetching: () -> Out,
    finished: (result: In) -> Out,
): Fetch.Finished<Out> {
    return fold(
        initial = { Fetch.Finished(initial()) },
        fetching = { Fetch.Finished(fetching()) },
        finished = { Fetch.Finished(finished(it)) },
    )
}
