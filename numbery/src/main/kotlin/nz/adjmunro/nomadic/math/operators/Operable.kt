package com.orbitremit.core.domain.model.math.operators

/**
 * Represents a type, [T], that supports basic math operators.
 *
 * @param T The type that implements this interface.
 *
 * @see Addable
 * @see Subtractable
 * @see Multipliable
 * @see Divisible
 * @see Comparable
 */
interface Operable<T> :
    Addable<T>,
    Subtractable<T>,
    Multipliable<T>,
    Divisible<T>,
    Comparable<T>
