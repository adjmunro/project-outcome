package nz.adjmunro.nomadic.math.tuple

data class Sextuple<out A, out B, out C, out D, out E, out F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
) : Tuple {

    override val size: Int = 6

    override fun toString(): String {
        return "Sextuple<6>($first, $second, $third, $fourth, $fifth, $sixth)"
    }

    operator fun <G> plus(seventh: G): Septuple<A, B, C, D, E, F, G> = Septuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
        seventh = seventh,
    )
}
