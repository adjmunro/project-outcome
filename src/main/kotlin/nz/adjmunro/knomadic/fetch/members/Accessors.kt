package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.inline.itself
import nz.adjmunro.inline.nulls
import kotlin.contracts.contract

/**
 * @return The [result][Fetch.Finished.result] of a [Fetch] or the [default] value.
 */
@KnomadicDsl
public infix fun <T: Any> Fetch<T>.getOrDefault(default: T): T {
    return fold(initial = { default }, fetching = { default }, finished = ::itself)
}

/**
 * @return The [result][Fetch.Finished.result] of a [Fetch] or the result of [recover].
 */
@KnomadicDsl
public inline fun <T> Fetch<T & Any>.getOrElse(recover: (Fetch<T & Any>) -> T): T {
    return fold(initial = { recover(this) }, fetching = { recover(this) }, finished = ::itself)
}

/**
 * @return The [result][Fetch.Finished.result] of a [Fetch] or `null`.
 */
@KnomadicDsl
public fun <T: Any> Fetch<T>.getOrNull(): T? {
    contract { 
        returnsNotNull() implies (this@getOrNull is Fetch.Finished<T>)
        returns(null) implies (this@getOrNull is Fetch.Initial || this@getOrNull is Fetch.Fetching)
    }
    
    return fold(initial = ::nulls, fetching = ::nulls, finished = ::itself)
}

/**
 * @return The [result][Fetch.Finished.result] of a [Fetch] or `throws`.
 * @throws IllegalStateException if the [Fetch] is a [Fetch.Initial] or [Fetch.Fetching].
 */
@KnomadicDsl
public fun <T: Any> Fetch<T>.getOrThrow(): T {
    contract { returns() implies (this@getOrThrow is Fetch.Finished<T>) }
    
    return fold(
        initial = { error("Fetch has not started!") },
        fetching = { error("Fetch has not finished!") },
        finished = ::itself,
    )
}
