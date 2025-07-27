package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.members.flatten

/** [Map] a [fetch flow][FetchFlow] to [flatten][Fetch.flatten] each emission. */
@KnomadicDsl
public fun <T : Any> FetchFlow<Fetch<T>>.flatten(): FetchFlow<T> = map { it.flatten }
