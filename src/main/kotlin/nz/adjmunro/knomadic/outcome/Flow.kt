package nz.adjmunro.knomadic.outcome

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

public fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.filterOnlySuccess(): Flow<Outcome.Success<Ok>> {
    return filterIsInstance<Outcome.Success<Ok>>()
}

public fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.filterOnlyFailure(): Flow<Outcome.Failure<Error>> {
    return filterIsInstance<Outcome.Failure<Error>>()
}

public inline fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.onEachSuccess(
    crossinline block: suspend (Ok) -> Unit
): Flow<Outcome<Ok, Error>> = onEach { it.onSuccess { value: Ok -> block(value) } }

public inline fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.onEachFailure(
    crossinline block: suspend (Error) -> Unit
): Flow<Outcome<Ok, Error>> = onEach { it.onFailure { error: Error -> block(error) } }

public inline fun <In: Any, Out: Any, Error: Any> Flow<Outcome<In, Error>>.mapEachSuccess(
    crossinline transform: suspend (In) -> Out
): Flow<Outcome<Out, Error>> = map { it.mapSuccess(transform) }

public inline fun <Ok: Any, ErrorIn: Any, ErrorOut: Any> Flow<Outcome<Ok, ErrorIn>>.mapEachFailure(
    crossinline transform: suspend (ErrorIn) -> ErrorOut
): Flow<Outcome<Ok, ErrorOut>> = map { it.mapFailure(transform) }

public fun <Ancestor : Any, Ok: Ancestor, Error: Ancestor> Flow<Outcome<Ok, Error>>.collapse(): Flow<Ancestor> =
    map { it.collapseToAncestor() }

public inline fun <Ok: Any, Error: Any, Output> Flow<Outcome<Ok, Error>>.collapse(
    crossinline success: suspend (Ok) -> Output,
    crossinline failure: suspend (Error) -> Output,
): Flow<Output> = map { it.collapseFold(success, failure) }
