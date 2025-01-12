package com.orbitremit.core.domain.model.math.operators

/**
 * Represents a type that can be multiplied with the same type, [T].
 *
 * @see [Operable]
 */
interface Multipliable<T> {
    /**
     * Product of both [T].
     *
     * @property [T] The type of the receiver & result.
     * @return Multiplication result as type [T].
     */
    operator fun times(other: T): T
}
