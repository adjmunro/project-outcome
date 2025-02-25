package nz.adjmunro.nomadic.error.fetch

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object FetchIs {
    inline val Fetch<*>.hasNotStarted: Boolean get() = this is Fetch.NotStarted
    inline val Fetch<*>.isInProgress: Boolean get() = this is Fetch.InProgress
    inline val Fetch<*>.isComplete: Boolean get() = this is Fetch.Completed<*>
    inline val Fetch<*>.hasStarted: Boolean get() = !hasNotStarted
    inline val Fetch<*>.isIncomplete: Boolean get() = !isComplete

    @OptIn(ExperimentalContracts::class)
    inline fun <T : Any> Fetch<T>.isNotStarted(): Boolean {
        contract { returns(true) implies (this is Fetch.NotStarted) }
        return this is Fetch.NotStarted
    }
}
