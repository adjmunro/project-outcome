package com.orbitremit.core.domain.model.math

/**
 * Interface for assisting value class operations.
 *
 * @param Inner Type of the [Boxed.value] (backing field) that is stored in the value class. (e.g. [kotlin.Double])
 * @param Outer Type of the value class (wrapper) that implements this interface.
 */
interface Boxed<Inner, Outer : Boxed<Inner, Outer>> {
    /** The backing field that is wrapped in the value class. */
    val value: Inner

    /**
     * Function reference to the constructor of the value class.
     *
     * ***You need to override this** in the value class itself, so that its
     * parent interfaces know how to create an instance of the child type.*
     *
     * ```kotlin
     * @JvmInline
     * value class ValueClass(override val value: Double) : Boxed<Double, ValueClass> {
     *     override val create: (Double) -> ValueClass
     *         get() = ::ValueClass
     * }
     * ```
     */
    val create: (Inner) -> Outer

    /**
     * Reduce two [Boxed] types into a new instance of the same type.
     *
     * *Specifically, this function unwraps the backing field of the receiver and [other],
     * applies the [transform] function to them, and then wraps the result into a new instance.*
     *
     * @param other The other [Boxed] type to reduce with.
     * @param transform The function to apply to the backing fields of the receiver and [other].
     * @return A new instance of the same type as the receiver, with the result of [transform].
     */
    fun reduce(other: Outer, transform: (Inner, Inner) -> Inner): Outer {
        return create(transform(this.value, other.value))
    }
}
