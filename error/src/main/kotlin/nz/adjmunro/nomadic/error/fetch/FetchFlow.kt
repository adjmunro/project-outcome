package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome
import kotlin.experimental.ExperimentalTypeInference

internal typealias FetchFlow<T> = Flow<Fetch<T>>

interface FetchFlowI<out T : Any> : Flow<Fetch<T>> {

    companion object {
        @NomadicDsl
        @OptIn(ExperimentalTypeInference::class)
        fun <T : Any> fetch(
            @BuilderInference block: suspend FetchFlowCollector<T>.() -> Unit,
        ): Flow<Fetch<T>> = SafeFetchFlow(block)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private class SafeFetchFlow<T : Any>(
        private val block: suspend FetchFlowCollector<T>.() -> Unit,
    ) : AbstractFlow<Fetch<T>>(), FetchFlow<T> {

        override suspend fun collectSafely(collector: FlowCollector<Fetch<T>>) {
            // Re-route to the `collectFetch` method
            collectFetch(fetchCollector = FetchFlowCollector(collector = collector))
        }

        private suspend fun collectFetch(fetchCollector: FetchFlowCollector<T>) {
            // Automatic `fetching` status
            fetchCollector.fetching()

            // Execute the block & await the result
            fetchCollector.block()
        }
    }
}
