@file:Suppress("NOTHING_TO_INLINE")

package nz.adjmunro.knomadic.util

import kotlinx.coroutines.CancellationException
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.KotlinResult
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Context runner to encapsulate the result of [block] as a [KotlinResult].
 *
 * - If [block] throws a [non-fatal][Throwable.isFatal] exception, the [Throwable] is encapsulated as a [Result.failure].
 * - If [block] throws a **[fatal][Throwable.isFatal]** exception, the [Throwable] is re-thrown!
 *
 * > For example, [CancellationException], which is necessary for Kotlin's
 * > structured concurrency model, is considered fatal and will always be rethrown.
 * > [Read more about issues with Kotlin's `runCatching()`](https://github.com/Kotlin/kotlinx.coroutines/issues/1814)
 *
 * @param T The return type of [block].
 * @param block The protected try-block which may throw and exception.
 * @throws Throwable See [Throwable.isFatal] for list of unsafe exceptions.
 * @see Throwable.nonFatalOrThrow
 */
@KnomadicDsl
public inline fun <T> resultOf(block: () -> T): KotlinResult<T> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }

    return try {
        success(value = block())
    } catch (e: Throwable) {
        failure(exception = e.nonFatalOrThrow())
    }
}

/**
 * Context runner that catches and normalises all [non-fatal][Throwable.isFatal]
 * thrown exceptions into a `null` value.
 *
 * *This is shorthand for `resultOf(block).getOrNull()`.*
 *
 * ```kotlin
 * // Example Usage:
 * val a: String? = nullable { throw Exception() } // == null
 * val b: String? = nullable { "Hello, World!" } // == "Hello, World!"
 * ```
 *
 * @param T The return type of [block].
 * @param block The protected try-block which may throw an exception.
 * @return The result of [block] or `null` if an exception was thrown.
 */
@KnomadicDsl
public inline fun <T> nullable(block: () -> T): T? {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return resultOf(block = block).getOrNull()
}

/**
 * Try to get the exception of this [Result].
 * - [Success][Result.isSuccess] -> `throws` a [NoSuchElementException]
 * - [Failure][Result.isFailure] -> `returns` the encapsulated [Throwable]
 */
@KnomadicDsl
public inline fun <T> Result<T>.exceptionOrThrow(): Throwable {
    return fold(
        onSuccess = { throw NoSuchElementException("Result is not a failure: $this") },
        onFailure = ::itself,
    )
}

/**
 * Try to get the exception of this [Result].
 * - [Success][Result.isSuccess] -> `returns` the result of [onSuccess] transformation.
 * - [Failure][Result.isFailure] -> `returns` the encapsulated [Throwable].
 *
 * *Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] function.*
 */
@KnomadicDsl
public inline fun <T> Result<T>.exceptionOrElse(onSuccess: (value: T) -> Throwable): Throwable {
    return fold(onSuccess = onSuccess, onFailure = ::itself)
}

/**
 * Try to get the exception of this [Result].
 * - [Success][Result.isSuccess] -> `returns` the [default] value.
 * - [Failure][Result.isFailure] -> `returns` the encapsulated [Throwable].
 */
@KnomadicDsl
public inline fun <T> Result<T>.exceptionOrDefault(default: Throwable): Throwable {
    return fold(onSuccess = { default }, onFailure = ::itself)
}

/**
 * Transforms [value][Result.value] inside a [resultOf] scope.
 * - [Success][Result.isSuccess] -> [wraps][resultOf] & `returns` the result of [onSuccess] transformation.
 * - [Failure][Result.isFailure] -> [wraps][resultOf] & `returns` the original [Throwable].
 * - *If [onSuccess] throws an exception, it will be caught & wrapped by [resultOf].*
 *
 * ***This is the [resultOf] alternative to [Result.mapCatching].***
 *
 * ```kotlin
 * resultOf { 4 }
 *   .andThen { it * 2 } // Result.success(8)
 *   .andThen { check(false) { it } } // Result.failure(IllegalStateException("4"))
 *   .andThen { 16 } // Remains Result.failure(IllegalStateException("4"))
 * ```
 *
 * @see resultOf
 * @see andIf
 * @see tryRecover
 */
@KnomadicDsl
public inline fun <I, O> KotlinResult<I>.andThen(onSuccess: (I) -> O): KotlinResult<O> {
    contract { callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE) }
    return fold(onSuccess = { resultOf { onSuccess(it) } }, onFailure = ::failure)
}

/**
 * If the [predicate] is `true`, transforms [value][Result.value] inside a [resultOf] scope.
 * - [Success][Result.isSuccess] -> [wraps][resultOf] & `returns` the result of [onSuccess] transformation.
 * - [Failure][Result.isFailure] -> [wraps][resultOf] & `returns` the original [Throwable].
 * - *If [onSuccess] throws an exception, it will be caught & wrapped by [resultOf].*
 *
 * ```kotlin
 * resultOf { 4 }.andIf({ it > 0 }) { it * 2 } // Result.success(8)
 * resultOf { 4 }.andIf({ it < 0 }) { it * 2 } // Result.success(4)
 * ```
 *
 * @see resultOf
 * @see andThen
 * @see tryRecover
 */
@KnomadicDsl
public inline fun <T> KotlinResult<T>.andIf(
    predicate: (T) -> Boolean,
    onSuccess: (T) -> T,
): KotlinResult<T> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }

    return andThen { if (predicate(it)) onSuccess(it) else it }
}

/**
 * Transform [exception][Result.Failure.exception] into [value][Result.value].
 * - [Success][Result.isSuccess] -> `returns` the original caller.
 * - [Failure][Result.isFailure] -> [wraps][resultOf] & `returns` the result of [onFailure] transformation.
 * - *If [onFailure] throws an exception, it will be caught & wrapped by [resultOf].*
 *
 * ***This is the [resultOf] alternative to [Result.recoverCatching].***
 *
 * ```kotlin
 * resultOf { 4 } // Result.success(4)
 *   .tryRecover { Unit } // No Change - Result.success(4)
 *   .andThen { throw FileNotFoundException("test") } // Result.failure(FileNotFoundException("test"))
 *   .tryRecover { 7 } // Result.success(7)
 * ```
 *
 * @see resultOf
 * @see andThen
 * @see mapFailure
 */
@KnomadicDsl
public inline fun <T> KotlinResult<T>.tryRecover(onFailure: (Throwable) -> T): KotlinResult<T> {
    contract { callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE) }
    return fold(onSuccess = ::caller, onFailure = { resultOf { onFailure(it) } })
}

/**
 * Transform [exception][Result.Failure.exception] into some other [Throwable].
 * - [Success][Result.isSuccess] -> `returns` the original caller.
 * - [Failure][Result.isFailure] -> [wraps][failure] & `returns` the result of [onFailure] transformation.
 *
 * *Note, this function rethrows any [Throwable] exception thrown by the [onFailure] function.*
 */
@KnomadicDsl
public inline fun <T> Result<T>.mapFailure(onFailure: (Throwable) -> Throwable): Result<T> {
    contract { callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE) }
    return fold(onSuccess = ::success, onFailure = { failure(onFailure(it)) })
}

/**
 * Like [map][Result.map], applies the [onSuccess] function to the encapsulated value.
 * - [Success][Result.isSuccess] -> `returns` the result of [onSuccess] transformation.
 * - [Failure][Result.isFailure] -> [wraps][failure] & `returns` the original [exception][Result.Failure.exception].
 *
 * *Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] function.
 * **However**, since this is [flatMap], you can easily embed another [resultOf] inside the [onSuccess] function.*
 */
@KnomadicDsl
public inline fun <In, Out> Result<In>.flatMap(
    @BuilderInference onSuccess: (In) -> Result<Out>,
): Result<Out> {
    return fold(onSuccess = onSuccess, onFailure = ::failure)
}

/**
 * Flattens a nested [KotlinResult].
 * - [Success][Result.isSuccess] -> `returns` the inner [KotlinResult]
 *   (which could be either a [success][Result.isSuccess] or [failure][Result.isFailure]).
 * - [Failure][Result.isFailure] -> `returns` a new [KotlinResult] with original
 *   [exception][Result.Failure.exception] from the outer [KotlinResult].
 */
@KnomadicDsl
public inline fun <T> Result<Result<T>>.flatten(): Result<T> {
    return fold(onSuccess = ::itself, onFailure = ::failure)
}

/**
 * - ***If all*** [results][KotlinResult] in the [Iterable] are [success][Result.isSuccess],
 *   `returns` a new [KotlinResult] success, with a [List] of each element's encapsulated value.
 * - ***If any*** [results][KotlinResult] in the [Iterable] are [failure][Result.isFailure],
 *   `returns` a new [KotlinResult] failure with the result of the [reduce] function.
 */
@KnomadicDsl
public inline fun <T> Iterable<KotlinResult<T>>.aggregate(
    reduce: (List<Throwable>) -> Throwable,
): KotlinResult<List<T>> {
    contract { callsInPlace(reduce, InvocationKind.AT_MOST_ONCE) }

    val (
        errors: List<KotlinResult<T>>,
        successes: List<KotlinResult<T>>,
    ) = partition(predicate = KotlinResult<T>::isFailure)

    return when {
        errors.isNotEmpty() -> failure(
            exception = reduce(errors.map(transform = KotlinResult<T>::exceptionOrThrow)),
        )

        else -> success(
            value = successes.map(transform = KotlinResult<T>::getOrThrow),
        )
    }
}
