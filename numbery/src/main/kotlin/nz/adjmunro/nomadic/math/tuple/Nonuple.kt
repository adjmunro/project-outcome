package nz.adjmunro.nomadic.math.tuple

data class Nonuple<out A, out B, out C, out D, out E, out F, out G, out H, out I>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
) : Tuple {

    override val size: Int = 9

    override fun toString(): String {
        return "Nonuple<9>($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth)"
    }

}
