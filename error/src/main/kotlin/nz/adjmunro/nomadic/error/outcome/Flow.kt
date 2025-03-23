package nz.adjmunro.nomadic.error.outcome

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.filterOnlySuccess(): Flow<Outcome.Success<Ok>> {
    return filterIsInstance<Outcome.Success<Ok>>()
}

fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.filterOnlyFailure(): Flow<Outcome.Failure<Error>> {
    return filterIsInstance<Outcome.Failure<Error>>()
}

inline fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.onEachSuccess(
    crossinline block: suspend (Ok) -> Unit
): Flow<Outcome<Ok, Error>> = onEach { it.onSuccess { value: Ok -> block(value) } }

inline fun <Ok : Any, Error : Any> Flow<Outcome<Ok, Error>>.onEachFailure(
    crossinline block: suspend (Error) -> Unit
): Flow<Outcome<Ok, Error>> = onEach { it.onFailure { error: Error -> block(error) } }

inline fun <In: Any, Out: Any, Error: Any> Flow<Outcome<In, Error>>.mapEachSuccess(
    crossinline transform: suspend (In) -> Out
): Flow<Outcome<Out, Error>> = map { it.mapSuccess(transform) }

inline fun <Ok: Any, ErrorIn: Any, ErrorOut: Any> Flow<Outcome<Ok, ErrorIn>>.mapEachFailure(
    crossinline transform: suspend (ErrorIn) -> ErrorOut
): Flow<Outcome<Ok, ErrorOut>> = map { it.mapFailure(transform) }

fun <Ancestor : Any, Ok: Ancestor, Error: Ancestor> Flow<Outcome<Ok, Error>>.collapse(): Flow<Ancestor> =
    map { it.collapseToAncestor() }

inline fun <Ok: Any, Error: Any, Output> Flow<Outcome<Ok, Error>>.collapse(
    crossinline success: suspend (Ok) -> Output,
    crossinline failure: suspend (Error) -> Output,
): Flow<Output> = map { it.collapseFold(success, failure) }
