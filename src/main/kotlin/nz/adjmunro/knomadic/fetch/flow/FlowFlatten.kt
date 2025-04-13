package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.flatten

/** Convenience function to apply [flatten] inside a [Flow]. */
public fun <T : Any> FetchFlow<Fetch<T>>.flatten(): FetchFlow<T> {
    return map { it.flatten }
}
