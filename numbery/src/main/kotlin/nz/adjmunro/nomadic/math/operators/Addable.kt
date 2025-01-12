package com.orbitremit.core.domain.model.math.operators

/**
 * Represents a type that can be added with the same type, [T].
 *
 * @see [Operable]
 */
interface Addable<T> {
    /**
     * Sum of both [T].
     *
     * @property [T] The type of the receiver & result.
     * @return Addition result as type [T].
     */
    operator fun plus(other: T): T
}

interface AutoAddable<A, B> {
    /**
     * Sum of [A] & B.
     *
     * @property [A] The type of the receiver.
     * @property [B] The type of the other & result.
     * @return Addition result as type [B].
     */
    operator fun plus(other: B): B
}

interface AutoAddable3<A, B, C> {
    /**
     * Sum of [A] & [B].
     *
     * @property [A] The type of the receiver.
     * @property [B] The type of the other & result.
     * @return Addition result as type [C].
     */
    operator fun plus(other: B): C
}
