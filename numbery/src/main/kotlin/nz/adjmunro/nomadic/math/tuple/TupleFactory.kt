package nz.adjmunro.nomadic.math.tuple

object TupleFactory {

    fun <A, B> tupleOf(
        first: A,
        second: B,
    ): Pair<A, B> = Pair(
        first = first,
        second = second,
    )

    fun <A, B, C> tupleOf(
        first: A,
        second: B,
        third: C,
    ): Triple<A, B, C> = Triple(
        first = first,
        second = second,
        third = third,
    )

    fun <A, B, C, D> tupleOf(
        first: A,
        second: B,
        third: C,
        fourth: D,
    ): Quadruple<A, B, C, D> = Quadruple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
    )

    fun <A, B, C, D, E> tupleOf(
        first: A,
        second: B,
        third: C,
        fourth: D,
        fifth: E,
    ): Quintuple<A, B, C, D, E> = Quintuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
    )

    fun <A, B, C, D, E, F> tupleOf(
        first: A,
        second: B,
        third: C,
        fourth: D,
        fifth: E,
        sixth: F,
    ): Sextuple<A, B, C, D, E, F> = Sextuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
    )

    fun <A, B, C, D, E, F, G> tupleOf(
        first: A,
        second: B,
        third: C,
        fourth: D,
        fifth: E,
        sixth: F,
        seventh: G,
    ): Septuple<A, B, C, D, E, F, G> = Septuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
        seventh = seventh,
    )

    fun <A, B, C, D, E, F, G, H> tupleOf(
        first: A,
        second: B,
        third: C,
        fourth: D,
        fifth: E,
        sixth: F,
        seventh: G,
        eighth: H,
    ): Octuple<A, B, C, D, E, F, G, H> = Octuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
        seventh = seventh,
        eighth = eighth,
    )

    fun <A, B, C, D, E, F, G, H, I> tupleOf(
        first: A,
        second: B,
        third: C,
        fourth: D,
        fifth: E,
        sixth: F,
        seventh: G,
        eighth: H,
        ninth: I,
    ): Nonuple<A, B, C, D, E, F, G, H, I> = Nonuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
        seventh = seventh,
        eighth = eighth,
        ninth = ninth,
    )

}
