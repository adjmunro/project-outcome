package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.outcome.Outcome
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Outcome.Success<Ok>)
        returns(false) implies (this@isSuccess is Outcome.Failure<Error>)
    }

    return this@isSuccess is Outcome.Success<Ok>
}

@KnomadicDsl
public fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure is Outcome.Failure<Error>)
        returns(false) implies (this@isFailure is Outcome.Success<Ok>)
    }

    return this@isFailure is Outcome.Failure<Error>
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isSuccess(
    predicate: (Ok) -> Boolean,
): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Outcome.Success<Ok>)
        returns(false) implies (this@isSuccess is Outcome.Failure<Error>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }

    return isSuccess() && predicate(value)
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isFailure(
    predicate: (Error) -> Boolean,
): Boolean {
    contract {
        returns(true) implies (this@isFailure is Outcome.Failure<Error>)
        returns(false) implies (this@isFailure is Outcome.Success<Ok>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }

    return isFailure() && predicate(error)
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.onSuccess(
    block: (Ok) -> Unit,
): Outcome<Ok, Error> {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    if (isSuccess()) block(value)
    return this@onSuccess
}

@KnomadicDsl
public inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.onFailure(
    block: (Error) -> Unit,
): Outcome<Ok, Error> {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    if (isFailure()) block(error)
    return this@onFailure
}
