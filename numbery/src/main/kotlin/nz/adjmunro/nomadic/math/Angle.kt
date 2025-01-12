@file:Suppress("Unused", "MemberVisibilityCanBePrivate")

package com.orbitremit.core.domain.model.math

import com.orbitremit.core.domain.model.math.Angle.Degrees
import com.orbitremit.core.domain.model.math.Angle.Radians
import com.orbitremit.core.domain.model.math.numbery.Doubly
import com.orbitremit.core.domain.model.math.operators.AutoAddable

/** Represents an [Angle] in [Degrees] or [Radians]. */
sealed interface Angle<T : Angle<T>> : Doubly<T> {

    /** Wrapper for an [Angle] in [Degrees]. */
    @JvmInline
    value class Degrees(override val value: Double) : Angle<Degrees>, AutoAddable<Degrees, Radians> {
        override val create: (Double) -> Degrees get() = ::Degrees

        /** [Comparable.compareTo] between [Degrees] and [Radians]. */
        operator fun compareTo(other: Radians): Int = compareTo(other.asRadians().asDouble)

        /** @return Adds [other] as [Radians]. */
        override operator fun plus(other: Radians): Radians = asRadians() + other

        /** @return Subtract [other] as [Radians]. */
        operator fun minus(other: Radians): Radians = asRadians() - other.value

        /** @return Multiplies [other] as [Radians]. */
        operator fun times(other: Radians): Radians = asRadians() * other.value

        /** @return Divides [other] as [Radians]. */
        operator fun div(other: Radians): Radians = asRadians() / other.value

        /** @return Remainder of receiver divided by [other] as [Radians]. */
        operator fun rem(other: Radians): Radians = asRadians() % other.value
    }

    /** Wrapper for an [Angle] in [Radians]. */
    @JvmInline
    value class Radians(override val value: Double) : Angle<Radians> {
        override val create: (Double) -> Radians get() = ::Radians

        /** [Comparable.compareTo] between [Radians] and [Degrees]. */
        operator fun compareTo(other: Degrees): Int = compareTo(other.asDegrees().asDouble)

        /** @return Adds [other] as [Degrees]. */
        operator fun plus(other: Degrees): Degrees = asDegrees() + other.value

        /** @return Subtract [other] as [Degrees]. */
        operator fun minus(other: Degrees): Degrees = asDegrees() - other.value

        /** @return Multiplies [other] as [Degrees]. */
        operator fun times(other: Degrees): Degrees = asDegrees() * other.value

        /** @return Divides [other] as [Degrees]. */
        operator fun div(other: Degrees): Degrees = asDegrees() / other.value

        /** @return Remainder of receiver divided by [other] as [Degrees]. */
        operator fun rem(other: Degrees): Degrees = asDegrees() % other.value
    }

    companion object {
        /** @return Any [Number] as [Angle.Degrees]. */
        val Number.degrees: Degrees get() = Degrees(value = toDouble())

        /** @return Any [Number] as [Angle.Radians]. */
        val Number.radians: Radians get() = Radians(value = toDouble())

        /** @return Converts [Degrees] into [Radians]. */
        fun Angle<*>.asRadians(): Radians = when (this) {
            is Degrees -> Radians(value = Math.toRadians(value))
            is Radians -> this
        }

        /** @return Converts [Radians] into [Degrees]. */
        fun Angle<*>.asDegrees(): Degrees = when (this) {
            is Radians -> Degrees(value = Math.toDegrees(value))
            is Degrees -> this
        }
    }
}
