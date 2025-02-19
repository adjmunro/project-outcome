@file:Suppress("MemberVisibilityCanBePrivate")

package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import nz.adjmunro.nomadic.error.fetch.Fetch.InProgress
import nz.adjmunro.nomadic.error.fetch.Fetch.NotStarted
import nz.adjmunro.nomadic.error.fetch.FetchFlowI.Companion.fetch
import nz.adjmunro.nomadic.error.maybe.MaybeOn.onNone
import nz.adjmunro.nomadic.error.outcome.OutcomeScope.outcomeOf
import nz.adjmunro.nomadic.error.outcome.OutcomeScope.outcomeRaise
import nz.adjmunro.nomadic.error.util.FlowUtils.onEachInstance
import nz.adjmunro.nomadic.error.util.some

object OnFetch {

    fun <T : Any> Flow<Fetch<T>>.asFetchFlow(): FetchFlow<T> = this as FetchFlow<T>

    fun <T : Any> Flow<Fetch<T>>.onEachFetch(
        action: suspend (Fetch<T>) -> Unit,
    ): FetchFlow<T> = onEach(action).asFetchFlow()

    val test = fetch {
        completed(some(1))

        val x = outcomeRaise({ error(it) }) {
            raise("")
            1
        }
    }.onNotStarted {

    }.onFetching {

    }.onCompleted {
        it.onNone { }
    }


    fun <T : Any> FetchFlow<T>.onNotStarted(action: suspend NotStarted.() -> Unit): FetchFlow<T> {
        onEachInstance<NotStarted>(action)
        return this
    }

    fun <T : Any> FetchFlow<T>.onFetching(action: suspend InProgress.() -> Unit): FetchFlow<T> {
        onEachInstance<InProgress>(action)
        return this
    }

    fun <T : Any> FetchFlow<T>.onCompleted(action: suspend (T) -> Unit): FetchFlow<T> {
        onEachInstance<Fetch.Completed<T>> { action(result) }
        return this
    }

    // TODO flatmap
}
