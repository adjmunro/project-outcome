package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.OutcomeFold.flatFold
import nz.adjmunro.nomadic.error.outcome.OutcomeFold.fold
import nz.adjmunro.nomadic.error.util.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeMap {

    @NomadicDsl
    inline fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.mapSuccess(
        @BuilderInference transform: (In) -> Out,
    ): Outcome<Out, Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return fold(
            success = transform,
            failure = ::identity,
        )
    }

    @NomadicDsl
    inline fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.mapFailure(
        @BuilderInference transform: (ErrorIn) -> ErrorOut,
    ): Outcome<Ok, ErrorOut> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return fold(
            success = ::identity,
            failure = transform,
        )
    }

    @NomadicDsl
    inline fun <In : Any, Out : Any, Error : Any> Outcome<In, Error>.flatMapSuccess(
        @BuilderInference transform: (In) -> Outcome<Out, Error>,
    ): Outcome<Out, Error> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return flatFold(
            success = transform,
            failure = { Outcome.Failure(it) },
        )
    }

    @NomadicDsl
    inline fun <Ok : Any, ErrorIn : Any, ErrorOut : Any> Outcome<Ok, ErrorIn>.flatMapFailure(
        @BuilderInference transform: (ErrorIn) -> Outcome<Ok, ErrorOut>,
    ): Outcome<Ok, ErrorOut> {
        contract {
            callsInPlace(transform, AT_MOST_ONCE)
        }

        return flatFold(
            success = { Outcome.Success(it) },
            failure = transform,
        )
    }

}
