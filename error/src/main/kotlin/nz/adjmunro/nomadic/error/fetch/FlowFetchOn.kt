@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.adjmunro.nomadic.error.FetchFlow
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fetch.Fetch.Finished
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

    fun <T : Any> FetchFlow<T>.onFinished(action: suspend (result: T) -> Unit): FetchFlow<T> {
        onEachInstance<Finished<T>> { action(result) }
        return this
    }

    fun <T : Any> FetchFlow<T>.onFinished(
        predicate: Boolean,
        action: suspend (result: T) -> Unit,
    ): FetchFlow<T> {
        if (predicate) onEachInstance<Finished<T>> { action(result) }
        return this
    }

    fun <T : Any> FetchFlow<T>.onFinished(
        predicate: suspend (result: T) -> Boolean,
        action: suspend (result: T) -> Unit,
    ): FetchFlow<T> {
        onEachInstance<Finished<T>> { if (predicate(result)) action(result) }
        return this
    }

    /**
     * Transform each [Fetch] status into a new [Fetch] status.
     *
     * ```
     * // IMPORTANT: finished must be provided! Consider:
     * inline fun <In : Any, Out : Any> FetchFlow<In>.fold(
     *     notStarted: NotStarted.() -> Fetch<Out> = { NotStarted },
     *     fetching: InProgress.() -> Fetch<Out> = { InProgress },
     *     finished: (result: In) -> Fetch<Out> = { Finished(it) },
     * ): FetchFlow<Out>
     *
     * // If either fetching or notStarted provide a Finished<Out>
     * fetchFlow.fold(fetching = { Finished(1) })
     *
     * // Then there is no way to ensure that Finished<In> and Finished<Out> are compatible!
     * flowOf(Finished(1)).fold(fetching = { Finished("a") }, /* finished = { Finished(1) } */)
     * ```
     */
    @OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
    @NomadicDsl
    inline fun <In : Any, Out : Any> FetchFlow<In>.fold(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline finished: suspend (result: In) -> Fetch<Out>,
    ): FetchFlow<Out> {
        contract {
            callsInPlace(notStarted, AT_MOST_ONCE)
            callsInPlace(fetching, AT_MOST_ONCE)
            callsInPlace(finished, AT_MOST_ONCE)
        }

        return map {
            when (it) {
                is NotStarted -> notStarted(it)
                is InProgress -> fetching(it)
                is Finished -> finished(it.result)
            }
        }
    }

    @OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
    @NomadicDsl
    inline fun <Ancestor: Any, In : Ancestor, Out : Ancestor> FetchFlow<In>.foldAncestor(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline finished: suspend (result: In) -> Fetch<Ancestor> = { Finished(it) },
    ): FetchFlow<Ancestor> {
        contract {
            callsInPlace(notStarted, AT_MOST_ONCE)
            callsInPlace(fetching, AT_MOST_ONCE)
            callsInPlace(finished, AT_MOST_ONCE)
        }

        return map {
            when (it) {
                is NotStarted -> notStarted(it)
                is InProgress -> fetching(it)
                is Finished<In> -> finished(it.result)
            }
        }
    }

    @OptIn(ExperimentalTypeInference::class)
    inline fun <In : Any, Out : Any> FetchFlow<In>.mapFinished(
        @BuilderInference crossinline transform: suspend (result: In) -> Fetch<Out>,
    ): FetchFlow<Out> = fold(finished = transform)

    @OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
    fun <In : Any, Out: Any> FetchFlow<In>.collapse(
        @BuilderInference notStarted: suspend NotStarted.() -> Out,
        @BuilderInference fetching: suspend InProgress.() -> Out,
        @BuilderInference finished: suspend (result: In) -> Out,
    ): Flow<Out> {
        contract {
            callsInPlace(notStarted, AT_MOST_ONCE)
            callsInPlace(fetching, AT_MOST_ONCE)
            callsInPlace(finished, AT_MOST_ONCE)
        }

        return map {
            when (it) {
                is NotStarted -> notStarted(it)
                is InProgress -> fetching(it)
                is Finished<In> -> finished(it.result)
            }
        }
    }

    fun <T: Any> FetchFlow<Fetch<T>>.flatten(): FetchFlow<T> = mapFinished { inner ->
        when (inner) {
            NotStarted, InProgress -> InProgress
            is Finished -> Finished(inner.result)
        }
    }
}
