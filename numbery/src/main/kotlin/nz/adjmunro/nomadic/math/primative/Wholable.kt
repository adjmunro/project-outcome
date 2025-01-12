package com.orbitremit.core.domain.model.math.primative

import java.math.BigInteger

/**
 * Represents a type that supports signed whole [Number] conversions.
 *
 * @see [Castable]
 */
interface Wholable {
    /** @return Converts the backing field into an [Int]. */
    val asInt: Int

    /** @return Converts the backing field into a [Long]. */
    val asLong: Long

    /** @return Converts the backing field into a [BigInteger]. */
    val asBigInteger: BigInteger
}
