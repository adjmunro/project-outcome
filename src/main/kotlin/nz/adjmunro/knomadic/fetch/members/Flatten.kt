package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.inline.itself
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.Finished
import nz.adjmunro.knomadic.fetch.Fetching

/**
 * Flatten a nested [Fetch] into a single [Fetch].
 */
@KnomadicDsl
public inline val <T : Any> Fetch<Fetch<T>>.flatten: Fetch<T>
    get() = fold(
        prefetch = ::itself,
        fetching = { cache ?: Fetching() },
        finished = Finished<Fetch<T>>::result,
    )
