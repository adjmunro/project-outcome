package nz.adjmunro.nomadic.math.tuple

object TripleExt {

    operator fun <A, B, C, D> Triple<A, B, C>.plus(
        fourth: D,
    ): Quadruple<A, B, C, D> = Quadruple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
    )

}
