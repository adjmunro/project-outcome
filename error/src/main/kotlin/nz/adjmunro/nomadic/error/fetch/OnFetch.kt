@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import nz.adjmunro.nomadic.error.fetch.Fetch.Completed
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.completed
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.fetch
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.fetching
import nz.adjmunro.nomadic.error.fetch.SafeFetchFlow.Companion.reset
import nz.adjmunro.nomadic.error.outcome.OutcomeGet.getOrNull
import nz.adjmunro.nomadic.error.outcome.OutcomeOn.onSuccess
import nz.adjmunro.nomadic.error.outcome.OutcomeScope.outcomeOf
import nz.adjmunro.nomadic.error.util.FlowTransformExt.bisect
import nz.adjmunro.nomadic.error.util.FlowTransformExt.mapInstance
import nz.adjmunro.nomadic.error.util.FlowTransformExt.onEachIf
import nz.adjmunro.nomadic.error.util.FlowTransformExt.onEachInstance
import nz.adjmunro.nomadic.error.util.FlowTransformExt.plus

object OnFetch {

    val t2 = flow {
        reset()
        fetching()
        completed(3)

        3
    }
    val test = fetch {
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

    fun <T : Any> FetchFlow<T>.onNotStarted(action: suspend NotStarted.() -> Unit): FetchFlow<T> {
        onEachInstance<NotStarted>(action)
        return this
    }

    fun <T : Any> FetchFlow<T>.onFetching(action: suspend InProgress.() -> Unit): FetchFlow<T> {
        onEachInstance<InProgress>(action)
        return this
    }

    fun <T : Any> FetchFlow<T>.onCompleted(action: suspend (result: T) -> Unit): FetchFlow<T> {
        onEachInstance<Completed<T>> { action(result) }
        return this
    }

    fun <T : Any> FetchFlow<T>.onCompleted(
        predicate: Boolean,
        action: suspend (result: T) -> Unit
    ): FetchFlow<T> {
        if (predicate) onEachInstance<Completed<T>> { action(result) }
        return this
    }

    fun <T : Any> FetchFlow<T>.onCompleted(
        predicate: suspend (result: T) -> Boolean,
        action: suspend (result: T) -> Unit
    ): FetchFlow<T> {
        onEachInstance<Completed<T>> { if (predicate(result)) action(result) }
        return this
    }

    // TODO flatmap
    // TODO map
    // TODO filter
    // TODO fold


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
                map { it as Number }.mapInstance<Double> { it + 1.0 }
                    .collect {
                    println(it)
                }
            }
        )
    }

    // TODO casting is broken for some reason
//    inline fun <In: Any, Out: Any> FetchFlow<In>.fold(
//        crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
//        crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
//        crossinline completed: suspend (result: In) -> Fetch<Out> = { Completed(result = it) },
//    ): FetchFlow<Out> {
//        return map {
//            when(it) {
//                is NotStarted -> notStarted(it)
//                is InProgress -> fetching(it)
//                is Completed -> completed(it.result)
//            }
//        }
//    }

//    fun <In: Any, Out: Any> FetchFlow<In>.mapNotStarted(
//        action: suspend NotStarted.() -> Fetch<Out>,
//    ): FetchFlow<Out> {
//        return map {
//            when(it) {
//                is NotStarted -> action(it)
//                is InProgress -> InProgress
//                is Completed -> Completed(it.result)
//            }
//        }
//    }
}
