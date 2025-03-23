@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.isFailure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Outcome.Success<Ok>)
        returns(false) implies (this@isSuccess is Outcome.Failure<Error>)
    }

    return this@isSuccess is Outcome.Success<Ok>
}

@NomadicDsl
fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure is Outcome.Failure<Error>)
        returns(false) implies (this@isFailure is Outcome.Success<Ok>)
    }

    return this@isFailure is Outcome.Failure<Error>
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isSuccess(
    @BuilderInference predicate: (Ok) -> Boolean,
): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Outcome.Success<Ok>)
        returns(false) implies (this@isSuccess is Outcome.Failure<Error>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }

    return isSuccess() && predicate(value)
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.isFailure(
    @BuilderInference predicate: (Error) -> Boolean,
): Boolean {
    contract {
        returns(true) implies (this@isFailure is Outcome.Failure<Error>)
        returns(false) implies (this@isFailure is Outcome.Success<Ok>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }

    return isFailure() && predicate(error)
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.onSuccess(
    @BuilderInference block: (Ok) -> Unit,
): Outcome<Ok, Error> {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    if (isSuccess()) block(value)
    return this@onSuccess
}

@NomadicDsl
inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.onFailure(
    @BuilderInference block: (Error) -> Unit,
): Outcome<Ok, Error> {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    if (isFailure()) block(error)
    return this@onFailure
}
