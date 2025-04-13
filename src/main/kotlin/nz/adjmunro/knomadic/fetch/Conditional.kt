package nz.adjmunro.knomadic.fetch

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

/** @return `true` if [Fetch] is [initial][Fetch.Initial]. */
@KnomadicDsl
public inline val <T : Any> Fetch<T>.isNotStarted: Boolean
    get() = this is Fetch.Initial

/** @return `true` if [Fetch] *is not* [initial][Fetch.Initial]. */
@KnomadicDsl
public inline val <T : Any> Fetch<T>.isStarted: Boolean
    get() = !isNotStarted

/** @return `true` if [Fetch] is [in-progress][Fetch.Fetching]. */
@KnomadicDsl
public inline val <T : Any> Fetch<T>.isFetching: Boolean
    get() = this is Fetch.Fetching

/** @return `true` if [Fetch] *is not* [in-progress][Fetch.Fetching]. */
@KnomadicDsl
public inline val <T : Any> Fetch<T>.isNotFetching: Boolean
    get() = !isFetching

/** @return `true` if [Fetch] *is not* [finished][Fetch.Finished]. */
@KnomadicDsl
public inline val <T : Any> Fetch<T>.isNotFinished: Boolean
    get() = !isFinished()

/** @return `true` if [Fetch] is [finished][Fetch.Finished]. */
@KnomadicDsl
public fun <T : Any> Fetch<T>.isFinished(): Boolean {
    contract { returns(true) implies (this@isFinished is Fetch.Finished<T>) }
    return this@isFinished is Fetch.Finished<T>
}

/** @return `true` if [predicate] is `true` *and* [Fetch] is [finished][Fetch.Finished]. */
@KnomadicDsl
public inline fun <T : Any> Fetch<T>.isFinished(predicate: (T) -> Boolean): Boolean {
    contract {
        returns(true) implies (this@isFinished is Fetch.Finished<T>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return isFinished() && predicate(result)
}

/** [Also][also] do an [action], if [Fetch] is [initial][Fetch.Initial]. */
@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onInitial(
    action: Fetch.Initial.() -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isNotStarted) action(Fetch.Initial)
    return this
}

/** [Also][also] do an [action], if [Fetch] is [in-progress][Fetch.Fetching]. */
@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFetching(
    action: Fetch.Fetching.() -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFetching) action(Fetch.Fetching)
    return this
}

/** [Also][also] do an [action], if [Fetch] is [finished][Fetch.Finished]. */
@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFinished(action: (T) -> Unit): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFinished()) action(result)
    return this
}

/** [Also][also] do an [action], if [predicate] is `true` *and* [Fetch] is [finished][Fetch.Finished]. */
@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFinished(
    predicate: Boolean,
    action: Fetch.Finished<T>.() -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (predicate && isFinished()) action(this)
    return this
}

/** [Also][also] do an [action], if [predicate] is `true` *and* [Fetch] is [finished][Fetch.Finished]. */
@KnomadicDsl
public inline fun <T : Any> Fetch<T>.onFinished(
    predicate: (result: T) -> Boolean,
    action: (T) -> Unit,
): Fetch<T> {
    contract { callsInPlace(action, AT_MOST_ONCE) }
    if (isFinished(predicate)) action(result)
    return this
}
