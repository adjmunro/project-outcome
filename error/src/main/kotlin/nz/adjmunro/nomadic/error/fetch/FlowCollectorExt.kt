package nz.adjmunro.nomadic.error.fetch

import kotlinx.coroutines.flow.FlowCollector
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import kotlin.experimental.ExperimentalTypeInference

object FlowCollectorExt {

    /**
     * [Emit][FlowCollector.emit] the result of [block], with a built-in [try-catch][recover].
     *
     * @param recover The transformation to apply to any [non-fatal][nonFatalOrThrow] [Throwable] that is caught.
     * @param block The block of code to execute.
     */
    @OptIn(ExperimentalTypeInference::class)
    @NomadicDsl
    suspend inline fun <T> FlowCollector<T>.emit(
        @BuilderInference recover: FlowCollector<T>.(Throwable) -> T = { throw it },
        @BuilderInference block: FlowCollector<T>.() -> T,
    ) {
        try {
            emit(block())
        } catch (e: Throwable) {
            emit(recover(e.nonFatalOrThrow()))
        }
    }

}
