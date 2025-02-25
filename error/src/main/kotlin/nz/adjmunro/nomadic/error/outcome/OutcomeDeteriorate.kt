package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.OutcomeFold.flatFold
import nz.adjmunro.nomadic.error.util.it
import nz.adjmunro.nomadic.error.util.receiver
import nz.adjmunro.nomadic.error.util.success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeDeteriorate {

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.deteriorate(
        @BuilderInference transform: (Ok) -> Error,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return flatFold(
            success = { Outcome.Failure(transform(it)) },
            failure = ::receiver
        )

//        return when (this@deteriorate) {
//            is Outcome.Success<Ok> -> Outcome.Failure(transform(value))
//            is Outcome.Failure<Error> -> this@deteriorate
//        }
    }

    @NomadicDsl
    inline infix fun <Ok : Any, Error : Any> Outcome<Ok, Error>.flatDeteriorate(
        @BuilderInference transform: (Ok) -> Outcome.Failure<Error>,
    ): Outcome<Ok, Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return flatFold(
            success = transform,
            failure = ::receiver
        )

//        return when (this@flatDeteriorate) {
//            is Outcome.Success<Ok> -> transform(value)
//            is Outcome.Failure<Error> -> this@flatDeteriorate
//        }
    }

}
