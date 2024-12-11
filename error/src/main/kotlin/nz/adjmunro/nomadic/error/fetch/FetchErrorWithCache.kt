package nz.adjmunro.nomadic.error.fetch

data class FetchErrorWithCache<out Ok : Any, out Error : Any>(
    val error: Error,
    val cache: Ok? = null,
)
