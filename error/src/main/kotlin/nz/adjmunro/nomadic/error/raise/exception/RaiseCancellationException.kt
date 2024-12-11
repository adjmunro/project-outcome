package nz.adjmunro.nomadic.error.raise.exception

import kotlin.coroutines.cancellation.CancellationException

class RaiseCancellationException(val error: Any) : CancellationException("Raise was cancelled!") {
    override fun fillInStackTrace(): Throwable {
        stackTrace = emptyArray()
        return this
    }
}
