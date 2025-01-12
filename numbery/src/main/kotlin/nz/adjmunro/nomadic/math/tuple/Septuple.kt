package nz.adjmunro.nomadic.math.tuple

data class Septuple<out A, out B, out C, out D, out E, out F, out G>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
) : Tuple {

    override val size: Int = 7

    override fun toString(): String {
        return "Septuple<7>($first, $second, $third, $fourth, $fifth, $sixth, $seventh)"
    }

    operator fun <H> plus(eighth: H): Octuple<A, B, C, D, E, F, G, H> = Octuple(
        first = first,
        second = second,
        third = third,
        fourth = fourth,
        fifth = fifth,
        sixth = sixth,
        seventh = seventh,
        eighth = eighth,
    )

}
