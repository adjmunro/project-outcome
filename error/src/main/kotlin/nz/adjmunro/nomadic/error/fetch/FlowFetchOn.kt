@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import nz.adjmunro.nomadic.error.FetchFlow
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fetch.Fetch.Finished
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import kotlin.experimental.ExperimentalTypeInference

object FlowFetchOn {

    fun <T : Any> FetchFlow<T>.filterOnlyFinished(): FetchFlow<T> {
        return filter { it.isFinished() }
    }

    fun <T : Any> FetchFlow<T>.onFetchNotStarted(
        action: suspend NotStarted.() -> Unit,
    ): FetchFlow<T> = onEach { if (it.hasNotStarted) action(NotStarted) }

    fun <T : Any> FetchFlow<T>.onFetchInProgress(
        action: suspend InProgress.() -> Unit,
    ): FetchFlow<T> = onEach { if (it.isInProgress) action(InProgress) }

    fun <T : Any> FetchFlow<T>.onFetchFinished(
        action: suspend (T) -> Unit,
    ): FetchFlow<T> = onEach { if (it.isFinished()) action(it.result) }

    fun <T : Any> FetchFlow<T>.onFetchFinished(
        predicate: Boolean,
        action: suspend (T) -> Unit,
    ): FetchFlow<T> = onEach { if (predicate && it.isFinished()) action(it.result) }

    fun <T : Any> FetchFlow<T>.onFetchFinished(
        predicate: (T) -> Boolean,
        action: suspend (T) -> Unit,
    ): FetchFlow<T> = onEach { if (it.isFinished(predicate)) action(it.result) }

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
    @OptIn(ExperimentalTypeInference::class)
    @NomadicDsl
    inline fun <In : Any, Out : Any> FetchFlow<In>.fold(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline finished: suspend (result: In) -> Fetch<Out>,
    ): FetchFlow<Out> = map {
        when (it) {
            is NotStarted -> notStarted(it)
            is InProgress -> fetching(it)
            is Finished -> finished(it.result)
        }
    }

    @OptIn(ExperimentalTypeInference::class)
    @NomadicDsl
    inline fun <Ancestor : Any, In : Ancestor, Out : Ancestor> FetchFlow<In>.foldAncestor(
        @BuilderInference crossinline notStarted: suspend NotStarted.() -> Fetch<Out> = { NotStarted },
        @BuilderInference crossinline fetching: suspend InProgress.() -> Fetch<Out> = { InProgress },
        @BuilderInference crossinline finished: suspend (result: In) -> Fetch<Ancestor> = {
            Finished(it)
        },
    ): FetchFlow<Ancestor> = map {
        when (it) {
            is NotStarted -> notStarted(it)
            is InProgress -> fetching(it)
            is Finished<In> -> finished(it.result)
        }
    }

    @OptIn(ExperimentalTypeInference::class)
    inline fun <In : Any, Out : Any> FetchFlow<In>.mapFinished(
        @BuilderInference crossinline transform: suspend (result: In) -> Fetch<Out>,
    ): FetchFlow<Out> = fold(finished = transform)

    @OptIn(ExperimentalTypeInference::class)
    fun <In : Any, Out : Any> FetchFlow<In>.collapse(
        @BuilderInference notStarted: suspend NotStarted.() -> Out,
        @BuilderInference fetching: suspend InProgress.() -> Out,
        @BuilderInference finished: suspend (result: In) -> Out,
    ): Flow<Out> = map {
        when (it) {
            is NotStarted -> notStarted(it)
            is InProgress -> fetching(it)
            is Finished<In> -> finished(it.result)
        }
    }

    fun <T : Any> FetchFlow<Fetch<T>>.flatten(): FetchFlow<T> = mapFinished { inner ->
        when (inner) {
            NotStarted, InProgress -> InProgress
            is Finished -> Finished(inner.result)
        }
    }
}
