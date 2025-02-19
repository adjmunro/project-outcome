package nz.adjmunro.nomadic.error.fetch

data class ErrorWithCache<out Ok : Any, out Error : Any>(
    val error: Error,
    val cache: Ok? = null,
) {
    val hasCache: Boolean get() = cache != null
}
