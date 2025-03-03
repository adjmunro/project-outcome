package nz.adjmunro.nomadic.error.util

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import nz.adjmunro.nomadic.error.R
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast
import kotlin.time.Duration

object FlowTransformExt {

    fun <T> Flow<T>.onEachIf(predicate: Boolean, action: suspend (T) -> Unit): Flow<T> {
        return if (predicate) onEach(action) else this
    }

    inline fun <T> Flow<T>.onEachIf(
        crossinline predicate: suspend (T) -> Boolean,
        crossinline action: suspend (T) -> Unit,
    ): Flow<T> {
        return onEach { value: T -> if(predicate(value)) action(value) }
    }

    // TODO flawed => return type is *not* Flow<T> but Flow<*>
    fun <T> Flow<T>.onEachInstance(klass: KClass<*>, action: suspend T.() -> Unit): Flow<T> {
//        filterIsInstance<T>().onEach(action)
//        return this
        return onEach { value -> if(klass.isInstance(value)) action(value) }
    }

    // TODO flawed => return type is *not* Flow<T> but Flow<*>
//    fun <T : Any, R> Flow<R>.onEachInstance(klass: KClass<T>, action: suspend T.() -> Unit): Flow<R> {
//        return onEach { if(klass.isInstance(it)) action(it as T) }
//    }

//    inline fun <reified T> Flow<*>.mapInstance(noinline action: suspend T.() -> T): Flow<*> {
//        filterIsInstance<T>().map(action) // todo should this return the result?
//        return this
//    }
//
//    fun <T : Any> Flow<*>.mapInstance(klass: KClass<T>, action: suspend T.() -> T): Flow<*> {
//        filterIsInstance(klass).map(action)
//        return this
//    }

    @OptIn(ExperimentalTypeInference::class)
    inline fun <A, reified B : A> Flow<A>.foldInstance(
        @BuilderInference crossinline no: suspend A.() -> A = { this },
        @BuilderInference crossinline yes: suspend B.() -> A,
    ): Flow<A> {
        return map { if (it is B) it.yes() else it.no() }
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

    // TODO move to async utils module?
    @OptIn(FlowPreview::class)
    inline fun <T> Flow<T>.recoverTimeout(
        duration: Duration,
        crossinline recover: suspend (TimeoutCancellationException) -> T,
    ): Flow<T> {
        return timeout(timeout = duration).catch { e: Throwable ->
            if (e is TimeoutCancellationException) emit(recover(e))
            else throw e
        }
    }
}
