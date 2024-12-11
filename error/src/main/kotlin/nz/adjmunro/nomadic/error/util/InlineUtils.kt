package nz.adjmunro.nomadic.error.util

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> identity(value: T): T {
    return value
}

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun throws(throwable: Throwable): Nothing {
    throw throwable
}
