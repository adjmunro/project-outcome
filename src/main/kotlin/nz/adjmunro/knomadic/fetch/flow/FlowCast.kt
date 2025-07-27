package nz.adjmunro.knomadic.fetch.flow

import kotlinx.coroutines.flow.map
import nz.adjmunro.knomadic.FaultyFlow
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.MaybeFlow
import nz.adjmunro.knomadic.OutcomeFlow
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.members.toFaulty
import nz.adjmunro.knomadic.fetch.members.toMaybe
import nz.adjmunro.knomadic.fetch.members.toOutcome


/** [Map] a [fetch flow][FetchFlow] to [outcome][Fetch.toOutcome] each emission. */
@KnomadicDsl
public fun <Ok : Any> FetchFlow<Ok>.mapToOutcome(): OutcomeFlow<Ok, Throwable> {
    return map(transform = Fetch<Ok>::toOutcome)
}

/** [Map] a [fetch flow][FetchFlow] to [maybe][Fetch.toMaybe] each emission. */
@KnomadicDsl
public fun <Ok : Any> FetchFlow<Ok>.mapToMaybe(): MaybeFlow<Ok> {
    return map(transform = Fetch<Ok>::toMaybe)
}

/** [Map] a [fetch flow][FetchFlow] to [faulty][Fetch.toFaulty] each emission. */
@KnomadicDsl
public fun FetchFlow<*>.mapToFaulty(): FaultyFlow<Throwable> {
    return map(transform = Fetch<*>::toFaulty)
}
