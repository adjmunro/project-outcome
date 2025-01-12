package nz.adjmunro.nomadic.math.tuple

data class Quintuple<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
) : Tuple {

    override val size: Int = 5

    override fun toString(): String {
        return "Quintuple<5>($first, $second, $third, $fourth, $fifth)"
    }

    operator fun <F> plus(sixth: F): Sextuple<A, B, C, D, E, F> = Sextuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
    )
}
