package nz.adjmunro.nomadic.math.tuple

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
) : Tuple {

    override val size: Int = 4

    override fun toString(): String {
        return "Quadruple<4>($first, $second, $third, $fourth)"
    }

    operator fun <E> plus(fifth: E): Quintuple<A, B, C, D, E> = Quintuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
    )

}
