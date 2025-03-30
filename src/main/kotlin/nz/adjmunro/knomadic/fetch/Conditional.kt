package nz.adjmunro.knomadic.fetch

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@KnomadicDsl
public inline val <T : Any> Fetch<T>.hasNotStarted: Boolean get() = this is Fetch.NotStarted

@KnomadicDsl
public inline val <T : Any> Fetch<T>.isInProgress: Boolean get() = this is Fetch.InProgress

@KnomadicDsl
public inline val <T : Any> Fetch<T>.isFetching: Boolean get() = this is Fetch.InProgress

@KnomadicDsl
public inline val <T : Any> Fetch<T>.hasStarted: Boolean get() = !hasNotStarted

@KnomadicDsl
public inline val <T : Any> Fetch<T>.isNotFinished: Boolean get() = !isFinished()

@KnomadicDsl
public fun <T : Any> Fetch<T>.isFinished(): Boolean {
    contract { returns(true) implies (this@isFinished is Fetch.Finished<T>) }
    return this@isFinished is Fetch.Finished<T>
}

@KnomadicDsl
public fun <T : Any> Fetch<T>.isFinished(@BuilderInference predicate: (T) -> Boolean): Boolean {
    contract {
        returns(true) implies (this@isFinished is Fetch.Finished<T>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return isFinished() && predicate(result)
}

@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onNotStarted(action: Fetch.NotStarted.() -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (hasNotStarted) action(Fetch.NotStarted)
    return this
}

@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onInProgress(action: Fetch.InProgress.() -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isInProgress) action(Fetch.InProgress)
    return this
}

@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFinished(@BuilderInference action: (T) -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFinished()) action(result)
    return this
}

@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFinished(
    predicate: Boolean,
    action: Fetch.Finished<T>.() -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (predicate && isFinished()) action(this)
    return this
}

@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFinished(
    @BuilderInference noinline predicate: (result: T) -> Boolean,
    @BuilderInference action: (T) -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFinished(predicate)) action(result)
    return this
}
