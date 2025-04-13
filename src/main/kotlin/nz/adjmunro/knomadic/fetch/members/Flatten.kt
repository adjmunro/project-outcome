package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch

/**
 * Flatten a nested [Fetch] into a single [Fetch].
 */
@KnomadicDsl
public inline val <T : Any> Fetch<Fetch<T>>.flatten: Fetch<T>
    get() = flatMapFinished { it }
