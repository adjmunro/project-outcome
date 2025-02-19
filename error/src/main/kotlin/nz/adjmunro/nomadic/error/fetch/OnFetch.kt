@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import nz.adjmunro.nomadic.error.fetch.Fetch.Completed
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import nz.adjmunro.nomadic.error.fetch.FetchFlowI.Companion.fetch
import nz.adjmunro.nomadic.error.maybe.MaybeOn.onNone
import nz.adjmunro.nomadic.error.util.FlowUtils.onEachInstance
import nz.adjmunro.nomadic.error.util.none
import nz.adjmunro.nomadic.error.util.some
import kotlin.experimental.ExperimentalTypeInference

object OnFetch {

    fun <T : Any> Flow<Fetch<T>>.asFetchFlow(): FetchFlow<T> = this as FetchFlow<T>

    fun <T : Any> Flow<Fetch<T>>.onEachFetch(
        action: suspend (Fetch<T>) -> Unit,
    ): FetchFlow<T> = onEach(action).asFetchFlow()

    val test = fetch {

        if(true) return@fetch some(3)
        reset()
        fetching()

        none()
    }.onNotStarted {

    }.onFetching {

    }.onCompleted {
        it.onNone {

        }
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

    // TODO flatmap
    // TODO map
    // TODO filter
    // TODO fold


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
