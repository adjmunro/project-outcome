package nz.adjmunro.knomadic.raise.exception

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal class RaiseCancellationException(val error: Any) : CancellationException("Raise was cancelled!") {
    override fun fillInStackTrace(): Throwable {
        stackTrace = emptyArray()
        return this
    }
}
