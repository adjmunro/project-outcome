package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import nz.adjmunro.nomadic.error.fetch.Fetch.Companion.fetch
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.FlowFetchOn.onCompleted
import nz.adjmunro.nomadic.error.fetch.FlowFetchOn.onFetching
import nz.adjmunro.nomadic.error.fetch.FlowFetchOn.onNotStarted
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.completed
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.fetching
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.reset
import nz.adjmunro.nomadic.error.outcome.OutcomeGet.getOrNull
import nz.adjmunro.nomadic.error.outcome.OutcomeOn.onSuccess
import nz.adjmunro.nomadic.error.outcome.OutcomeScope.outcomeOf
import nz.adjmunro.nomadic.error.util.FlowTransformExt.bisect
import nz.adjmunro.nomadic.error.util.FlowTransformExt.onEachIf
import nz.adjmunro.nomadic.error.util.FlowTransformExt.plus
import kotlin.time.Duration.Companion.seconds

object FetchScratchTest {

    val t2 = flow {
        reset()
        fetching()
        completed(3)

        3
    }
    val test = fetch(timeout = 100.seconds, { InProgress }) {
        outcomeOf<Int, Throwable> { 4 }
    }.onNotStarted {

    }.onFetching {

    }.onCompleted {

    }.onCompleted(false) {

    }.onCompleted({ it.getOrNull() == 4 }) {
        it.onSuccess {

        }
    }.onEachIf({ it is InProgress }) {

    }.onEachIf(true) {
        it as InProgress
    }

    suspend fun test() {
        flowOf(1, 2, 3).bisect(
            predicate = { true },
            trueBranch = {
                (map { it.toString() } + flowOf("a", "b", "c"))
                    .collect {
                        println(it)
                    }
            },
            falseBranch = {
//                map { it as Number }.mapInstance<Double> { it + 1.0 }
//                    .collect {
//                        println(it)
//                    }
            }
        )
    }
}
