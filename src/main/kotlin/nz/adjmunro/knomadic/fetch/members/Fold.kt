package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Transform each [Fetch] status into an [Output].
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
public inline fun <T : Any, Output: Any?> Fetch<T>.fold(
    initial: () -> Output,
    fetching: () -> Output,
    finished: (result: T) -> Output,
): Output {
    contract { 
        callsInPlace(initial, InvocationKind.AT_MOST_ONCE)
        callsInPlace(fetching, InvocationKind.AT_MOST_ONCE)
        callsInPlace(finished, InvocationKind.AT_MOST_ONCE)
    }
    
    return when (this@fold) {
        is Fetch.Initial -> initial()
        is Fetch.Fetching -> fetching()
        is Fetch.Finished -> finished(result)
    }
}
