package nz.adjmunro.nomadic.math.tuple

object TupleCollections {

    fun <T> Quadruple<T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth)

    fun <T> Quintuple<T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth)

    fun <T> Sextuple<T, T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth, sixth)

    fun <T> Septuple<T, T, T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth, sixth, seventh)

    fun <T> Octuple<T, T, T, T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth, sixth, seventh, eighth)

    fun <T> Nonuple<T, T, T, T, T, T, T, T, T>.toList(): List<T> =
        listOf(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth)

}
