package nz.adjmunro.knomadic.fetch

import nz.adjmunro.knomadic.KnomadicDsl

/**
 * Flatten a nested [Fetch] into a single [Fetch].
 */
@KnomadicDsl
public inline val <T : Any> Fetch<Fetch<T>>.flatten: Fetch<T>
    get() = flatMapFinished { it }
