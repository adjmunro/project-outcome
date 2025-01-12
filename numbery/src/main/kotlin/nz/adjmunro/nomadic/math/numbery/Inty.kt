@file:Suppress("Unused")

package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed

/** Interface for [Boxed] value classes of [Int]. */
interface Inty<Actual : Inty<Actual>> : Wholly<Int, Actual> {
    override val value: Int
    override val plus: (Int, Int) -> Int get() = Int::plus
    override val minus: (Int, Int) -> Int get() = Int::minus
    override val times: (Int, Int) -> Int get() = Int::times
    override val div: (Int, Int) -> Int get() = Int::div
    override val rem: (Int, Int) -> Int get() = Int::rem
    override val compare: (Int, Int) -> Int get() = Int::compareTo
    override val normalise: (Number) -> Int get() = Number::toInt
}
