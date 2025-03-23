@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package nz.adjmunro.nomadic.error.fetch

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@NomadicDsl
inline val <T : Any> Fetch<T>.hasNotStarted: Boolean get() = this is Fetch.NotStarted

@NomadicDsl
inline val <T : Any> Fetch<T>.isInProgress: Boolean get() = this is Fetch.InProgress

@NomadicDsl
inline val <T : Any> Fetch<T>.isFetching: Boolean get() = this is Fetch.InProgress

@NomadicDsl
inline val <T : Any> Fetch<T>.hasStarted: Boolean get() = !hasNotStarted

@NomadicDsl
inline val <T : Any> Fetch<T>.isNotFinished: Boolean get() = !isFinished()

@NomadicDsl
fun <T : Any> Fetch<T>.isFinished(): Boolean {
    contract { returns(true) implies (this@isFinished is Fetch.Finished<T>) }
    return this@isFinished is Fetch.Finished<T>
}

@NomadicDsl
fun <T : Any> Fetch<T>.isFinished(@BuilderInference predicate: (T) -> Boolean): Boolean {
    contract {
        returns(true) implies (this@isFinished is Fetch.Finished<T>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return isFinished() && predicate(result)
}

@NomadicDsl
inline fun <T : Any> Fetch<T>.onNotStarted(action: Fetch.NotStarted.() -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (hasNotStarted) action(Fetch.NotStarted)
    return this
}

@NomadicDsl
inline fun <T : Any> Fetch<T>.onInProgress(action: Fetch.InProgress.() -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isInProgress) action(Fetch.InProgress)
    return this
}

@NomadicDsl
inline fun <T : Any> Fetch<T>.onFinished(@BuilderInference action: (T) -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFinished()) action(result)
    return this
}

@NomadicDsl
inline fun <T : Any> Fetch<T>.onFinished(
    predicate: Boolean,
    action: Fetch.Finished<T>.() -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (predicate && isFinished()) action(this)
    return this
}

@NomadicDsl
inline fun <T : Any> Fetch<T>.onFinished(
    @BuilderInference noinline predicate: (result: T) -> Boolean,
    @BuilderInference action: (T) -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFinished(predicate)) action(result)
    return this
}
