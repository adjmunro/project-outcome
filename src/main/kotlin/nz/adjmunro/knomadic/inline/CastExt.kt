package nz.adjmunro.knomadic.inline

import nz.adjmunro.knomadic.KnomadicDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

/**
 * Casts [Any] to a type [T] or throw a [ClassCastException].
 *
 * *Chained function call alternative to `this as T`.*
 *
 * @throws ClassCastException if the cast fails.
 * @return The cast value of type [T].
 * @see castOrNull
 * @see castOrElse
 */
public inline fun <reified T> Any.castOrThrow(): T {
    contract { returns().implies(this@castOrThrow is T) }
    return this@castOrThrow as T
}

/**
 * Casts [Any] to a type [T] or returns `null`.
 *
 * *Chained function call alternative to `this as? T`.*
 *
 * @return The cast value of type [T] or `null` if the cast fails.
 * @see castOrThrow
 * @see castOrElse
 */
public inline fun <reified T> Any?.castOrNull(): T? {
    contract { returns(null).implies(this@castOrNull !is T) }
    return this@castOrNull as? T
}

/**
 * Casts [Any] to a type [T] or returns a [default] value.
 *
 * ```
 * // Chained function call alternative to:
 * this as? T ?: default        // Default value
 * this as? T ?: return@scope   // Early escape
 * this as? T ?: run { ... }    // If-null block
 * this as? T ?: throw IllegalStateException("Custom Exception")
 * ```
 *
 * @param default A lambda that provides a default value of type [T].
 * @return The cast value of type [T] or the result of the [default] lambda if the cast fails.
 * @see castOrThrow
 * @see castOrNull
 */
@KnomadicDsl
public inline fun <reified T> Any?.castOrElse(default: () -> T): T = this as? T ?: default()

/**
 * Checks if the [Any] is an instance of type [T].
 *
 * *Chained function call alternative to `if(this is T)`.*
 *
 * @return `true` if the [Any] is an instance of type [T], otherwise `false`.
 */
@KnomadicDsl
public inline fun <reified T> Any?.instanceOf(): Boolean {
    contract { returns(true).implies(this@instanceOf is T) }
    return this is T
}

/**
 * Executes the [block] if the [Parent] is an instance of type [Child].
 *
 * ```
 * // Chained function call alternative to:
 * receiver.also { if(this is Child) block(this) }
 *
 * // Example usage:
 * receiver.on(Child::class) { ... }
 * ```
 *
 * @param instanceof The [KClass] of the [Child] type.
 * @param block The lambda to execute if the [Parent] is an instance of type [Child].
 * @return The original [Parent] object.
 */
@Suppress("unused")
@KnomadicDsl
public inline fun <Parent, reified Child : Any> Parent.on(
    instanceof: KClass<Child>,
    block: Child.() -> Unit,
): Parent {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    castOrNull<Child>()?.block()
    return this
}
