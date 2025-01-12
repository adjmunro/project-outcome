package nz.adjmunro.nomadic.math.tuple

object PairExt {

    operator fun <A, B, C> Pair<A, B>.plus(
        third: C,
    ): Triple<A, B, C> = Triple(
        first = first,
        second = second,
        third = third,
    )

}
