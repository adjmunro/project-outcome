package nz.adjmunro.knomadic.outcome.members

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import nz.adjmunro.knomadic.OutcomeFlow
import nz.adjmunro.knomadic.outcome.Outcome

public fun <Ok : Any, Error : Any> OutcomeFlow<Ok, Error>.filterOnlySuccess(): Flow<Ok> {
    return filterIsInstance<Outcome.Success<Ok>>().map { it.value }
}

public fun <Ok : Any, Error : Any> OutcomeFlow<Ok, Error>.filterOnlyFailure(): Flow<Error> {
    return filterIsInstance<Outcome.Failure<Error>>().map { it.error }
}

public inline fun <Ok : Any, Error : Any> OutcomeFlow<Ok, Error>.onEachSuccess(
    crossinline block: suspend (Ok) -> Unit
): OutcomeFlow<Ok, Error> = onEach { it.onSuccess { value: Ok -> block(value) } }

public inline fun <Ok : Any, Error : Any> OutcomeFlow<Ok, Error>.onEachFailure(
    crossinline block: suspend (Error) -> Unit
): OutcomeFlow<Ok, Error> = onEach { it.onFailure { error: Error -> block(error) } }

public inline fun <In: Any, Out: Any, Error: Any> OutcomeFlow<In, Error>.mapEachSuccess(
    crossinline transform: suspend (In) -> Out
): OutcomeFlow<Out, Error> = map { it.mapSuccess { transform(it) } }

public inline fun <Ok: Any, ErrorIn: Any, ErrorOut: Any> OutcomeFlow<Ok, ErrorIn>.mapEachFailure(
    crossinline transform: suspend (ErrorIn) -> ErrorOut
): OutcomeFlow<Ok, ErrorOut> = map { it.mapFailure { transform(it) } }

public fun <Ancestor : Any, Ok: Ancestor, Error: Ancestor> OutcomeFlow<Ok, Error>.collapse(): Flow<Ancestor> =
    map { it.collapse() }

public inline fun <Ok: Any, Error: Any, Output> OutcomeFlow<Ok, Error>.collapse(
    crossinline success: suspend (Ok) -> Output,
    crossinline failure: suspend (Error) -> Output,
): Flow<Output> = map { it.fold({ success(it) }, { failure(it) }) }
