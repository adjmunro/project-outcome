package nz.adjmunro.knomadic.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import nz.adjmunro.knomadic.FetchFlow
import nz.adjmunro.knomadic.KnomadicDsl

public fun <T : Any> FetchFlow<T>.filterOnlyFinished(): FetchFlow<T> {
    return filter { it.isFinished() }
}

public inline fun <T : Any> FetchFlow<T>.onFetchNotStarted(
    @BuilderInference crossinline action: suspend Fetch.NotStarted.() -> Unit,
): FetchFlow<T> = onEach { if (it.hasNotStarted) action(Fetch.NotStarted) }

public inline fun <T : Any> FetchFlow<T>.onFetchInProgress(
    @BuilderInference crossinline action: suspend Fetch.InProgress.() -> Unit,
): FetchFlow<T> = onEach { if (it.isInProgress) action(Fetch.InProgress) }

public inline fun <T : Any> FetchFlow<T>.onFetchFinished(
    @BuilderInference crossinline action: suspend (T) -> Unit,
): FetchFlow<T> = onEach { if (it.isFinished()) action(it.result) }

public inline fun <T : Any> FetchFlow<T>.onFetchFinished(
    predicate: Boolean,
    @BuilderInference crossinline action: suspend (T) -> Unit,
): FetchFlow<T> = onEach { if (predicate && it.isFinished()) action(it.result) }

public inline fun <T : Any> FetchFlow<T>.onFetchFinished(
    @BuilderInference noinline predicate: (T) -> Boolean,
    @BuilderInference crossinline action: suspend (T) -> Unit,
): FetchFlow<T> = onEach { if (it.isFinished(predicate)) action(it.result) }

/**
 * Transform each [Fetch] status into a new [Fetch] status.
 *
 * ```
 * // IMPORTANT: finished must be provided! Consider:
 public * inline fun <In : Any, Out : Any> FetchFlow<In>.fold(
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
@KnomadicDsl
public suspend fun <In : Any, Out : Any> FetchFlow<In>.fold(
    @BuilderInference notStarted: suspend Fetch.NotStarted.() -> Fetch<Out> = { Fetch.NotStarted },
    @BuilderInference fetching: suspend Fetch.InProgress.() -> Fetch<Out> = { Fetch.InProgress },
    @BuilderInference finished: suspend (result: In) -> Fetch<Out>,
): FetchFlow<Out> = map {
    when (it) {
        is Fetch.NotStarted -> notStarted(it)
        is Fetch.InProgress -> fetching(it)
        is Fetch.Finished -> finished(it.result)
    }
}

@KnomadicDsl
public inline fun <Ancestor : Any, In : Ancestor, Out : Ancestor> FetchFlow<In>.foldAncestor(
    @BuilderInference crossinline notStarted: suspend Fetch.NotStarted.() -> Fetch<Out> = { Fetch.NotStarted },
    @BuilderInference crossinline fetching: suspend Fetch.InProgress.() -> Fetch<Out> = { Fetch.InProgress },
    @BuilderInference crossinline finished: suspend (result: In) -> Fetch<Ancestor> = {
        Fetch.Finished(it)
    },
): FetchFlow<Ancestor> = map {
    when (it) {
        is Fetch.NotStarted -> notStarted(it)
        is Fetch.InProgress -> fetching(it)
        is Fetch.Finished<In> -> finished(it.result)
    }
}

public suspend fun <In : Any, Out : Any> FetchFlow<In>.mapFinished(
    @BuilderInference transform: suspend (result: In) -> Fetch<Out>,
): FetchFlow<Out> = fold(finished = transform)

public suspend fun <In : Any, Out : Any> FetchFlow<In>.collapse(
    @BuilderInference notStarted: suspend Fetch.NotStarted.() -> Out,
    @BuilderInference fetching: suspend Fetch.InProgress.() -> Out,
    @BuilderInference finished: suspend (result: In) -> Out,
): Flow<Out> = map {
    when (it) {
        is Fetch.NotStarted -> notStarted(it)
        is Fetch.InProgress -> fetching(it)
        is Fetch.Finished<In> -> finished(it.result)
    }
}

public suspend fun <T : Any> FetchFlow<Fetch<T>>.flatten(): FetchFlow<T> = mapFinished { inner ->
    when (inner) {
        Fetch.NotStarted, Fetch.InProgress -> Fetch.InProgress
        is Fetch.Finished -> Fetch.Finished(inner.result)
    }
}
