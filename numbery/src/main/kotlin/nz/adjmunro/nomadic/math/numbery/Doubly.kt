@file:Suppress("Unused")

package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed

/** Interface for [Boxed] value classes of [Double]. */
interface Doubly<Actual : Doubly<Actual>> : Decimally<Double, Actual> {
    override val value: Double
    override val plus: (Double, Double) -> Double get() = Double::plus
    override val minus: (Double, Double) -> Double get() = Double::minus
    override val times: (Double, Double) -> Double get() = Double::times
    override val div: (Double, Double) -> Double get() = Double::div
    override val rem: (Double, Double) -> Double get() = Double::rem
    override val compare: (Double, Double) -> Int get() = Double::compareTo
    override val normalise: (Number) -> Double get() = Number::toDouble
}
