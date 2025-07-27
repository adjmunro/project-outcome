package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.inline.caller
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.Finished
import nz.adjmunro.knomadic.fetch.Fetching
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Transforms [Fetching.cache] in place.
 *
 * This is a convenience method for [fold][Fetch.fold], that
 * only transforms one state and **wraps the output as [Fetching]**.
 *
 * *If [In] != [Out], then [Out] = the closest common ancestor of [In] and [Out].*
 *
 * @receiver The [Fetch] to transform.
 * @param In The type of the [Fetch] value.
 * @param Out The type of the [transformed][transform] value.
 * @param transform The lambda to transform the [Fetching] state.
 * @return The maybe transformed [Fetch], now of type [Out].
 */
@KnomadicDsl
public inline fun <In: Out, Out: Any> Fetch<In>.mapFetching(
    transform: Fetching<In>.() -> Out?,
): Fetch<Out> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }

    return fold(
        prefetch = ::caller,
        fetching = { Fetching(cache = transform()) },
        finished = ::caller,
    )
}

/**
 * Transforms [Finished.result] in place.
 *
 * This is a convenience method for [fold][Fetch.fold], that
 * only transforms one state and **wraps the output as [Finished]**.
 *
 * *If [In] != [Out], then [Out] = the closest common ancestor of [In] and [Out].*
 *
 * @receiver The [Fetch] to transform.
 * @param In The type of the [Fetch] value.
 * @param Out The type of the [transformed][transform] value.
 * @param transform The lambda to transform the [Finished] state.
 * @return The maybe transformed [Fetch], now of type [Out].
 */
@KnomadicDsl
public inline fun <In: Out, Out: Any> Fetch<In>.mapFinished(
    transform: Finished<In>.() -> Out,
): Fetch<Out> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }

    return fold(
        prefetch = ::caller,
        fetching = ::caller,
        finished = { Finished(result = transform()) },
    )
}

/**
 * Transforms [Finished.result] into [Fetching.cache].
 *
 * *If [In] != [Out], then [Out] = the closest common ancestor of [In] and [Out].*
 *
 * @receiver The [Fetch] to transform.
 * @param In The type of the [Fetch] value.
 * @param Out The type of the [transformed][transform] value.
 * @param transform The lambda to transform the [Finished] state.
 * @return The maybe transformed [Fetch], now of type [Out].
 */
@KnomadicDsl
public inline fun <In: Out, Out: Any> Fetch<In>.mapToCache(
    transform: Finished<In>.() -> Out? = Finished<In>::result,
): Fetch<Out> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }

    return fold(
        prefetch = ::caller,
        fetching = ::caller,
        finished = { Fetching(cache = transform()) },
    )
}
