package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed
import com.orbitremit.core.domain.model.math.operators.AutoAddable3
import com.orbitremit.core.domain.model.math.operators.Operable
import com.orbitremit.core.domain.model.math.primative.Castable

/**
 * Interface for any [Number]-based value class that is [Operable].
 *
 * @param Inner Type of the [Boxed.value] (backing field) that is stored in the value class. (e.g. [kotlin.Double]).
 * @param Outer Type of the value class (wrapper) that implements this interface.
 */
interface Numbery<Inner : Number, Outer : Numbery<Inner, Outer>> :
    AutoAddable3<Outer, Number, Outer>,
    Boxed<Inner, Outer>,
    Castable,
    Operable<Outer> {
    /** Function reference to the plus operator for [Inner]. */
    val plus: (Inner, Inner) -> Inner

    /** Function reference to the minus operator for [Inner]. */
    val minus: (Inner, Inner) -> Inner

    /** Function reference to the times operator for [Inner]. */
    val times: (Inner, Inner) -> Inner

    /** Function reference to the div operator for [Inner]. */
    val div: (Inner, Inner) -> Inner

    /** Function reference to the rem operator for [Inner]. */
    val rem: (Inner, Inner) -> Inner

    /** Function reference to the compareTo operator for [Inner]. */
    val compare: (Inner, Inner) -> Int

    /** Function reference to the [Number] converter function that produces [Inner]. */
    val normalise: (Number) -> Inner

    /**
     * Assuming [other] is the same unit quantity as the receiver, returns the sum of the two.
     *
     * @property [Outer] The type of the receiver & result.
     * @return Addition result as type [Outer].
     */
    override operator fun plus(other: Number): Outer = create(plus(value, normalise(other)))

    /**
     * Assuming [other] is the same unit quantity as the receiver, returns the difference of the two.
     *
     * @property [Outer] The type of the receiver & result.
     * @return Subtraction result as type [Outer].
     */
    operator fun minus(other: Number): Outer = create(minus(value, normalise(other)))

    /**
     * Assuming [other] is the same unit quantity as the receiver, returns the product of the two.
     *
     * @property [Outer] The type of the receiver & result.
     * @return Multiplication result as type [Outer].
     */
    operator fun times(other: Number): Outer = create(times(value, normalise(other)))

    /**
     * Assuming [other] is the same unit quantity as the receiver,
     * returns the quotient of the receiver divided by [other].
     *
     * @property [Outer] The type of the receiver & result.
     * @return Division result as type [Outer].
     */
    operator fun div(other: Number): Outer = create(div(value, normalise(other)))

    /**
     * Assuming [other] is the same unit quantity as the receiver,
     * returns the remainder of the receiver divided by [other].
     *
     * @property [Outer] The type of the receiver & result.
     * @return Remainder result as type [Outer].
     */
    operator fun rem(other: Number): Outer = create(rem(value, normalise(other)))

    /**
     * Assuming [other] is the same unit quantity as the receiver,
     * returns the comparison of the receiver to [other].
     *
     * @property [Outer] The type of the receiver & result.
     * @return Comparison result as type [Outer].
     */
    operator fun compareTo(other: Number): Int = compare(value, normalise(other))

    override operator fun plus(other: Outer): Outer = reduce(other = other, transform = plus)
    override operator fun minus(other: Outer): Outer = reduce(other = other, transform = minus)
    override operator fun times(other: Outer): Outer = reduce(other = other, transform = times)
    override operator fun div(other: Outer): Outer = reduce(other = other, transform = div)
    override operator fun rem(other: Outer): Outer = reduce(other = other, transform = rem)
    override operator fun compareTo(other: Outer): Int = compare(value, other.value)
}
