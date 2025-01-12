package com.orbitremit.core.domain.model.math.operators

/**
 * Represents a type that can be subtracted with the same type, [T].
 *
 * @see [Operable]
 */
interface Subtractable<T> {
    /**
     * Difference of both [T].
     *
     * @property [T] The type of the receiver & result.
     * @return Subtraction result as type [T].
     */
    operator fun minus(other: T): T
}
