@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.nomadic.error.FetchFlow
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fetch.Fetch.Completed
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import nz.adjmunro.nomadic.error.util.FlowTransformExt.onEachInstance
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
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
        action: suspend (result: T) -> Unit,
    ): FetchFlow<T> {
        if (predicate) onEachInstance<Completed<T>> { action(result) }
        return this
    }

    fun <T : Any> FetchFlow<T>.onCompleted(
        predicate: suspend (result: T) -> Boolean,
        action: suspend (result: T) -> Unit,
    ): FetchFlow<T> {
        onEachInstance<Completed<T>> { if (predicate(result)) action(result) }
        return this
    }

    /**
     * Transform each [Fetch] status into a new [Fetch] status.
     *
     * ```
     * // IMPORTANT: completed must be provided! Consider:
     * inline fun <In : Any, Out : Any> FetchFlow<In>.fold(
     *     notStarted: NotStarted.() -> Fetch<Out> = { NotStarted },
     *     fetching: InProgress.() -> Fetch<Out> = { InProgress },
     *     completed: (result: In) -> Fetch<Out> = { Completed(it) },
     * ): FetchFlow<Out>
     *
     * // If either fetching or notStarted provide a Completed<Out>
     * fetchFlow.fold(fetching = { Completed(1) })
     *
     * // Then there is no way to ensure that Completed<In> and Completed<Out> are compatible!
     * flowOf(Completed(1)).fold(fetching = { Completed("a") }, /* completed = { Completed(1) } */)
     * ```
     */
    @OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
    @NomadicDsl
    inline fun <In : Any, Out : Any> FetchFlow<In>.fold(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline completed: suspend (result: In) -> Fetch<Out>,
    ): FetchFlow<Out> {
        contract {
            callsInPlace(notStarted, AT_MOST_ONCE)
            callsInPlace(fetching, AT_MOST_ONCE)
            callsInPlace(completed, AT_MOST_ONCE)
        }

        return map {
            when (it) {
                is NotStarted -> notStarted(it)
                is InProgress -> fetching(it)
                is Completed -> completed(it.result)
            }
        }
    }

    @OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
    @NomadicDsl
    inline fun <Ancestor: Any, In : Ancestor, Out : Ancestor> FetchFlow<In>.foldAncestor(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline completed: suspend (result: In) -> Fetch<Ancestor> = { Completed(it) },
    ): FetchFlow<Ancestor> {
        contract {
            callsInPlace(notStarted, AT_MOST_ONCE)
            callsInPlace(fetching, AT_MOST_ONCE)
            callsInPlace(completed, AT_MOST_ONCE)
        }

        return map {
            when (it) {
                is NotStarted -> notStarted(it)
                is InProgress -> fetching(it)
                is Completed<In> -> completed(it.result)
            }
        }
    }

    @OptIn(ExperimentalTypeInference::class)
    inline fun <In : Any, Out : Any> FetchFlow<In>.mapCompleted(
        @BuilderInference crossinline transform: suspend (result: In) -> Fetch<Out>,
    ): FetchFlow<Out> = fold(completed = transform)

    @OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
    fun <In : Any, Out: Any> FetchFlow<In>.collapse(
        @BuilderInference notStarted: suspend NotStarted.() -> Out,
        @BuilderInference fetching: suspend InProgress.() -> Out,
        @BuilderInference completed: suspend (result: In) -> Out,
    ): Flow<Out> {
        contract {
            callsInPlace(notStarted, AT_MOST_ONCE)
            callsInPlace(fetching, AT_MOST_ONCE)
            callsInPlace(completed, AT_MOST_ONCE)
        }

        return map {
            when (it) {
                is NotStarted -> notStarted(it)
                is InProgress -> fetching(it)
                is Completed<In> -> completed(it.result)
            }
        }
    }

    fun <T: Any> FetchFlow<Fetch<T>>.flatten(): FetchFlow<T> = mapCompleted { inner ->
        when (inner) {
            NotStarted, InProgress -> InProgress
            is Completed -> Completed(inner.result)
        }
    }
}
