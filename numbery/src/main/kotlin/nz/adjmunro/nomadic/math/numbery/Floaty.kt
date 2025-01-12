@file:Suppress("Unused")

package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed

/** Interface for [Boxed] value classes of [Float]. */
interface Floaty<Actual : Floaty<Actual>> : Decimally<Float, Actual> {
    override val value: Float
    override val plus: (Float, Float) -> Float get() = Float::plus
    override val minus: (Float, Float) -> Float get() = Float::minus
    override val times: (Float, Float) -> Float get() = Float::times
    override val div: (Float, Float) -> Float get() = Float::div
    override val rem: (Float, Float) -> Float get() = Float::rem
    override val compare: (Float, Float) -> Int get() = Float::compareTo
    override val normalise: (Number) -> Float get() = Number::toFloat
}
