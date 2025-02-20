package nz.adjmunro.nomadic.error.fetch

import nz.adjmunro.nomadic.error.fetch.FetchIs.hasNotStarted
import nz.adjmunro.nomadic.error.util.StandardExt.applyIf
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed interface Fetch<out T : Any> {

    data object NotStarted : Fetch<Nothing>

    data object InProgress : Fetch<Nothing>

    @JvmInline
    value class Completed<T : Any>(val result: T) : Fetch<T>
}

object FetchIs {
    inline val Fetch<*>.hasNotStarted: Boolean get() = this is Fetch.NotStarted
    inline val Fetch<*>.isInProgress: Boolean get() = this is Fetch.InProgress
    inline val Fetch<*>.isComplete: Boolean get() = this is Fetch.Completed<*>
    inline val Fetch<*>.hasStarted: Boolean get() = !hasNotStarted
    inline val Fetch<*>.isIncomplete: Boolean get() = !isComplete

    @OptIn(ExperimentalContracts::class)
    inline fun <T: Any> Fetch<T>.isNotStarted(): Boolean {
        contract { returns(true) implies (this is Fetch.NotStarted) }
        return this is Fetch.NotStarted
    }
}

object FetchOn {
    inline fun <T : Any> Fetch<T>.onNotStarted(action: Fetch.NotStarted.() -> Unit): Fetch<T> {
        if (hasNotStarted) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onInProgress(action: Fetch.InProgress.() -> Unit): Fetch<T> {
        if (this is Fetch.InProgress) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onCompleted(action: Fetch.Completed<T>.() -> Unit): Fetch<T> {
        if (this is Fetch.Completed) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onCompleted(
        crossinline predicate: (result: T) -> Boolean,
        action: Fetch.Completed<T>.() -> Unit
    ): Fetch<T> {
        if (this is Fetch.Completed && predicate(result)) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onCompleted(
        predicate: Boolean,
        action: Fetch.Completed<T>.() -> Unit
    ): Fetch<T> {
        if (this is Fetch.Completed && predicate) action(this)
        return this
    }
}
