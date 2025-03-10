package nz.adjmunro.nomadic.error.fetch

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object FetchIs {
    inline val Fetch<*>.hasNotStarted: Boolean get() = this is Fetch.NotStarted
    inline val Fetch<*>.isInProgress: Boolean get() = this is Fetch.InProgress
    inline val Fetch<*>.isFinished: Boolean get() = this is Fetch.Finished<*>
    inline val Fetch<*>.hasStarted: Boolean get() = !hasNotStarted
    inline val Fetch<*>.isNotFinished: Boolean get() = !isFinished

    @OptIn(ExperimentalContracts::class)
    inline fun <T : Any> Fetch<T>.isNotStarted(): Boolean {
        contract { returns(true) implies (this@isNotStarted is Fetch.NotStarted) }
        return this is Fetch.NotStarted
    }
}
