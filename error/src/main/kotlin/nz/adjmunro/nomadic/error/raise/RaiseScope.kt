package nz.adjmunro.nomadic.error.raise

import kotlinx.atomicfu.atomic
import nz.adjmunro.nomadic.error.raise.exception.RaiseCancellationException
import nz.adjmunro.nomadic.error.raise.exception.RaiseScopeLeakedException

interface RaiseScope<in Error : Any> {

    fun raise(error: Error): Nothing

    fun <Ok : Any> (RaiseScope<Error>.() -> Ok).injectRaiseScope(): Ok {
        return this(this@RaiseScope)
    }

    suspend fun <Ok : Any> (suspend RaiseScope<Error>.() -> Ok).injectRaiseScope(): Ok {
        return this(this@RaiseScope)
    }

    class Default<in Error : Any> : RaiseScope<Error> {
        private val active = atomic(true)

        @PublishedApi
        internal fun complete(): Boolean = active.getAndSet(false)

        override fun raise(error: Error): Nothing {
            when (active.value) {
                true -> throw RaiseCancellationException(error)
                false -> throw RaiseScopeLeakedException()
            }
        }
    }
}
