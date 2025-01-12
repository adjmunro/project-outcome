@file:Suppress("Unused")

package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/** Interface for [Boxed] value classes of decimal [Number]s. */
interface Decimally<Inner : Number, Outer : Numbery<Inner, Outer>> : Numbery<Inner, Outer> {
    override val asBigInteger: BigInteger get() = asLong.toBigInteger()
    override val asFloat: Float get() = value.toFloat()
    override val asDouble: Double get() = value.toDouble()
    override val asBigDecimal: BigDecimal get() = BigDecimal.valueOf(asDouble)

    /** @return Round [Inner] backing field to an [Int] (via [Double.roundToInt]). */
    override val asInt: Int get() = asDouble.roundToInt()

    /** @return Round [Inner] backing field to a [Long] (via [Double.roundToLong]). */
    override val asLong: Long get() = asDouble.roundToLong()
}
