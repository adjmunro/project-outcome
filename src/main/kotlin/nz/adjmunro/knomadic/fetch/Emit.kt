package nz.adjmunro.knomadic.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import nz.adjmunro.knomadic.FetchCollector
import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.util.nonFatalOrThrow

/**
 * [Send][FlowCollector.emit] a [fetch not started][Fetch.Initial]
 * status to the current [flow-scope][Flow].
 */
@KnomadicDsl
public suspend inline fun FetchCollector<Nothing>.reset() {
    emit(Fetch.Initial)
}

/**
 * [Send][FlowCollector.emit] a [fetch in progress][Fetch.Fetching]
 * status to the current [flow-scope][Flow].
 */
@KnomadicDsl
public suspend inline fun FetchCollector<Nothing>.fetching() {
    emit(Fetch.Fetching)
}

/**
 * [Send][FlowCollector.emit] a [fetch finished][Fetch.Finished]
 * status to the current [flow-scope][Flow], with the encapsulated [result].
 */
@KnomadicDsl
public suspend inline fun <T : Any> FetchCollector<T>.finished(result: T) {
    emit(Fetch.Finished(result = result))
}

/**
 * [Emit][FlowCollector.emit] the result of [block], with a built-in [try-catch][recover].
 *
 * @param recover The transformation to apply to any [non-fatal][nonFatalOrThrow] [Throwable] that is caught.
 * @param block The block of code to execute.
 */
@KnomadicDsl
public suspend inline fun <T> FlowCollector<T>.emit(
    recover: FlowCollector<T>.(Throwable) -> T = { throw it },
    @BuilderInference block: FlowCollector<T>.() -> T,
) {
    try {
        emit(block())
    } catch (e: Throwable) {
        emit(recover(e.nonFatalOrThrow()))
    }
}
