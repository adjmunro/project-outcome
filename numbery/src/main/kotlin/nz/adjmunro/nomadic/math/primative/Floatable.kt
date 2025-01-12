package com.orbitremit.core.domain.model.math.primative

import java.math.BigDecimal

/**
 * Represents a type that supports floating point [Number] conversions.
 *
 * @see [Castable]
 */
interface Floatable {
    /** @return Converts the backing field into a [Float]. */
    val asFloat: Float

    /** @return Converts the backing field into a [Double]. */
    val asDouble: Double

    /** @return Converts the backing field into a [BigDecimal]. */
    val asBigDecimal: BigDecimal
}
