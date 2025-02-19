package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import nz.adjmunro.nomadic.error.NomadicDsl

@JvmInline
value class FetchFlowCollector<in T : Any>(
    private val collector: FlowCollector<Fetch<T>>,
) : FlowCollector<Fetch<T>> by collector {

    /**
     * [Send][FlowCollector.emit] a [fetch not started][Fetch.NotStarted]
     * status to the current [flow-scope][Flow].
     */
    @NomadicDsl
    suspend inline fun reset() {
        emit(Fetch.NotStarted)
    }

    /**
     * [Send][FlowCollector.emit] a [fetch in progress][Fetch.InProgress]
     * status to the current [flow-scope][Flow].
     */
    @NomadicDsl
    suspend inline fun fetching() {
        emit(Fetch.InProgress)
    }

    /**
     * [Send][FlowCollector.emit] a [fetch completed][Fetch.Completed]
     * status to the current [flow-scope][Flow], with the encapsulated [result].
     */
    @NomadicDsl
    suspend inline fun completed(result: T) {
        emit(Fetch.Completed(result = result))
    }
}
