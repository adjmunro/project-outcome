package com.orbitremit.core.domain.model.math.operators

/**
 * Represents a type that can be divided with the same type, [T].
 *
 * @see [Operable]
 */
interface Divisible<T> {
    /**
     * Quotient of receiver divided by [other].
     *
     * @property [T] The type of the receiver & result.
     * @return Division result as type [T].
     */
    operator fun div(other: T): T

    /**
     * Remainder of receiver divided by [other].
     *
     * @property [T] The type of the receiver & result.
     * @return Remainder result as type [T].
     */
    operator fun rem(other: T): T
}
