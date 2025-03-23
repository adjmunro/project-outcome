package nz.adjmunro.knomadic.fetch

public data class ErrorWithCache<out Ok : Any, out Error : Any>(
    public val error: Error,
    public val cache: Ok? = null,
) {
    public val hasCache: Boolean
        get() = cache != null
}
