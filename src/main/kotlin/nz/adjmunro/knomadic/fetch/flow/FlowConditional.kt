package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.isFinished

/**
 * Filter a [FetchFlow] by [isFinished], and map to the [result][Fetch.Finished.result].
 *
 * @receiver The [FetchFlow] to filter.
 * @param T The type of the [Fetch] value.
 * @return A [Flow] of [T], the unwrapped [result][Fetch.Finished.result] values.
 */
public fun <T : Any> FetchFlow<T>.filterOnlyFinished(): Flow<T> {
    return filter { it.isFinished() }.mapNotNull { (it as? Fetch.Finished<T>)?.result }
}

/**
 * Perform non-mutating actions according to [Fetch] state [on each][Flow.onEach] flow emission.
 *
 * @receiver The [FetchFlow] to perform actions on.
 * @param T The type of the [Fetch] value.
 * @param initial The action to perform when the [Fetch] is [initial][Fetch.Initial].
 * @param fetching The action to perform when the [Fetch] is [in-progress][Fetch.Fetching].
 * @param fetched The action to perform when the [Fetch] is [finished][Fetch.Finished].
 * @return The original [FetchFlow].
 */
public fun <T : Any> FetchFlow<T>.onEachFetch(
    initial: suspend Fetch.Initial.() -> Unit = {},
    fetching: suspend Fetch.Fetching.() -> Unit = {},
    fetched: suspend Fetch.Finished<T>.(T) -> Unit = {},
): FetchFlow<T> = onEach { fetch ->
    when (fetch) {
        is Fetch.Initial -> initial(fetch)
        is Fetch.Fetching -> fetching(fetch)
        is Fetch.Finished -> fetched(fetch, fetch.result)
    }
}
