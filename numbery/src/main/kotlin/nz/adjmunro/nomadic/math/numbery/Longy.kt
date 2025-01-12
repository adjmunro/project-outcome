@file:Suppress("Unused")

package com.orbitremit.core.domain.model.math.numbery

import com.orbitremit.core.domain.model.math.Boxed

/** Interface for [Boxed] value classes of [Long]. */
interface Longy<Actual : Longy<Actual>> : Wholly<Long, Actual> {
    override val value: Long
    override val plus: (Long, Long) -> Long get() = Long::plus
    override val minus: (Long, Long) -> Long get() = Long::minus
    override val times: (Long, Long) -> Long get() = Long::times
    override val div: (Long, Long) -> Long get() = Long::div
    override val rem: (Long, Long) -> Long get() = Long::rem
    override val compare: (Long, Long) -> Int get() = Long::compareTo
    override val normalise: (Number) -> Long get() = Number::toLong
}
