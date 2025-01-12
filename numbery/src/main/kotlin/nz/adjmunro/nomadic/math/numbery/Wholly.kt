@file:Suppress("Unused")

package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed
import java.math.BigDecimal
import java.math.BigInteger

/** Interface for [Boxed] value classes of whole [Number]s. */
interface Wholly<Inner : Number, Outer : Numbery<Inner, Outer>> : Numbery<Inner, Outer> {
    override val asInt: Int get() = value.toInt()
    override val asLong: Long get() = value.toLong()
    override val asBigInteger: BigInteger get() = asLong.toBigInteger()
    override val asFloat: Float get() = value.toFloat()
    override val asDouble: Double get() = value.toDouble()
    override val asBigDecimal: BigDecimal get() = BigDecimal.valueOf(asDouble)
}
