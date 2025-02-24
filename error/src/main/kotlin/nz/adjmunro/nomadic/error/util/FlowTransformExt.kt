package nz.adjmunro.nomadic.error.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.reflect.KClass

object FlowTransformExt {

    fun <T> Flow<T>.onEachIf(predicate: Boolean, action: suspend (T) -> Unit): Flow<T> {
        return if (predicate) onEach(action) else this
    }

    inline fun <T> Flow<T>.onEachIf(
        crossinline predicate: suspend (T) -> Boolean,
        noinline action: suspend (T) -> Unit,
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

    inline fun <reified T> Flow<*>.mapInstance(noinline action: suspend T.() -> T): Flow<*> {
        filterIsInstance<T>().map(action) // todo should this return the result?
        return this
    }

    fun <T: Any> Flow<*>.mapInstance(klass: KClass<T>, action: suspend T.() -> T): Flow<*> {
        filterIsInstance(klass).map(action)
        return this
    }

    operator fun <A, B> Flow<A>.plus(other: Flow<B>): Flow<Pair<A, B>> =
        combine(other) { a, b -> a to b }

    operator fun <A, B> Flow<A>.times(other: Flow<B>): Flow<Pair<A, B>> =
        zip(other) { a, b -> a to b }

    operator fun <T> Flow<T>.minus(filter: T): Flow<T> = filterNot { it == filter }


    suspend fun <T> FlowCollector<T>.pond(
        tributary: suspend () -> Flow<T>,
    ): Job = supervisorScope {
        launch { tributary().collect { this@pond.emit(it) } }
    }

    suspend fun <T> FlowCollector<T>.pond(
        vararg tributaries: suspend () -> Flow<T>,
    ): List<Job> = supervisorScope {
        tributaries.map { tributary ->
            launch { tributary().collect { this@pond.emit(it) } }
        }
    }

    suspend fun <T> Flow<T>.bisect(
        predicate: suspend (T) -> Boolean,
        trueBranch: suspend Flow<T>.() -> Unit,
        falseBranch: suspend Flow<T>.() -> Unit,
    ) {
        supervisorScope {
            launch { filter(predicate).trueBranch() }
            launch { filterNot(predicate).falseBranch() }
        }
    }
}
