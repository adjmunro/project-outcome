@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.internal.NopCollector.emit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import nz.adjmunro.nomadic.error.FetchCollector
import nz.adjmunro.nomadic.error.FetchFlow
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fetch.Fetch.Completed
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import nz.adjmunro.nomadic.error.fetch.FlowCollectorExt.emit
import nz.adjmunro.nomadic.error.util.FlowTransformExt.onEachInstance
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

object FlowFetchOn {

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

    // TODO casting is broken for some reason
    @OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
    @NomadicDsl
    inline fun <In: Any, Out: Any> FetchFlow<In>.fold(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline completed: suspend FetchCollector<Out>.(result: In) -> Fetch<Out> = { Completed(result = it) },
    ): FetchFlow<Out> {
        contract {
            callsInPlace(lambda = notStarted, kind = InvocationKind.AT_MOST_ONCE)
            callsInPlace(lambda = fetching, kind = InvocationKind.AT_MOST_ONCE)
            callsInPlace(lambda = completed, kind = InvocationKind.AT_MOST_ONCE)
        }

        return transform { if (it is Completed) completed(it.result) }
//            emit {
//                when(it) {
//                    is NotStarted -> notStarted(it)
//                    is InProgress -> fetching(it)
//                    is Completed -> transform<In, Out> { completed(it.result) }
//                }
//            }
        }
    }

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
