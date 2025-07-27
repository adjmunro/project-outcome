package nz.adjmunro.knomadic.fetch.members

import nz.adjmunro.inline.nulls
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.fetch.Fetch
import nz.adjmunro.knomadic.fetch.Fetching
import nz.adjmunro.knomadic.fetch.Finished
import nz.adjmunro.knomadic.fetch.Prefetch
import kotlin.contracts.contract

/**
 * Unwrap the internal value of a [Fetch], or [default].
 * - [Prefetch] returns [default];
 * - [Fetching] returns [cache] (or [default] if `null`);
 * - [Finished] returns [result].
 *
 * @return A value of type [T].
 */
@KnomadicDsl
public infix fun <T: Any> Fetch<T>.getOrDefault(default: T): T {
    return fold(prefetch = { default }, fetching = { cache ?: default }, finished = Finished<T>::result)
}

/**
 * Unwrap the internal value of a [Fetch], or [recover].
 * - [Prefetch] returns the result of [recover];
 * - [Fetching] returns [cache] (or [recover] if `null`);
 * - [Finished] returns [result].
 *
 * @return A value of type [T].
 */
@KnomadicDsl
public inline fun <T: Any> Fetch<T>.getOrElse(recover: Fetch<T>.() -> T): T {
    return fold(prefetch = recover, fetching = { cache ?: recover() }, finished = Finished<T>::result)
}

/**
 * Unwrap the internal value of a [Fetch], or `null`.
 * - [Prefetch] returns `null`;
 * - [Fetching] returns [cache] (or null);
 * - [Finished] returns [result].
 *
 * @return A value of type [T], or `null`.
 */
@KnomadicDsl
public fun <T: Any> Fetch<T>.getOrNull(): T? {
    contract { returnsNotNull() implies (this@getOrNull is Finished<T>) }
    return fold(prefetch = ::nulls, fetching = Fetching<T>::cache, finished = Finished<T>::result)
}

/**
 * Unwrap the internal value of a [Fetch], or [throw][IllegalStateException].
 *
 * - [Prefetch] throws an [IllegalStateException];
 * - [Fetching] returns [cache] (or [throws][IllegalStateException]);
 * - [Finished] returns [result].
 *
 * @return A value of type [T].
 * @throws IllegalStateException if the [Fetch] is a [Prefetch] or [Fetching.cache] is `null`.
 */
@KnomadicDsl
public fun <T: Any> Fetch<T>.getOrThrow(): T {
    contract { returns(null) implies (this@getOrThrow is Prefetch) }
    
    return fold(
        prefetch = { error("Fetch has not started!") },
        fetching = { cache ?: error("Fetch has not finished! (no result or cache)") },
        finished = Finished<T>::result,
    )
}

/**
 * Attempt to unwrap a [Fetch] to obtain it's [Finished.result] or [Fetching.cache].
 * 
 * ```kotlin
 * val fetch: Fetch<String> = Prefetch
 * fetch.unwrap()           // getOrThrow() (default behaviour)
 * fetch.unwrap { null }    // getOrNull() (prefetch & fetching to null)
 * fetch.unwrap { "$it" }   // getOrElse() (prefetch & fetching to string)
 * ```
 * 
 * @return A value of type [Out], or the result of [recover].
 * @throws IllegalStateException if default [recover] value is used and fetch is [prefetch][Prefetch] or [fetching][Fetching] with no cache.
 * @see Fetch.getOrThrow
 * @see Fetch.getOrNull
 * @see Fetch.getOrElse
 * @see Fetch.getOrDefault
 */
@KnomadicDsl
public inline fun <In : Out & Any, Out: Any?> Fetch<In>.unwrap(
    recover: Fetch<In>.() -> Out = { error("Fetch has not finished! (no result or cache)") },
): Out = fold(prefetch = recover, fetching = { cache ?: recover() }, finished = Finished<In>::result)
