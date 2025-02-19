package nz.adjmunro.nomadic.error.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

object FlowUtils {

    fun <T> Flow<T>.onEach(predicate: Boolean, action: suspend T.() -> Unit): Flow<T> {
        return if (predicate) onEach(action) else this
    }

    inline fun <T> Flow<T>.onEach(
        crossinline predicate: suspend (T) -> Boolean,
        noinline action: suspend T.() -> Unit,
    ): Flow<T> {
        filter(predicate).onEach(action)
        return this
    }

    inline fun <reified T> Flow<*>.onEachInstance(noinline action: suspend T.() -> Unit): Flow<*> {
        filterIsInstance<T>().onEach(action)
        return this
    }

    fun <T : Any> Flow<*>.onEachInstance(klass: KClass<T>, action: suspend T.() -> Unit): Flow<*> {
        filterIsInstance(klass).onEach(action)
        return this
    }

    // TODO
    fun <In, Out> Flow<In>.mapInstance(klass: KClass<Out>, action: suspend Out.() -> Unit): Flow<In> {
        filterIsInstance(klass).onEach(action)
        return this
    }

}
