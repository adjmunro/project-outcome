package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeFold {

    @NomadicDsl
    inline fun <In : Any, Out : Any, ErrorIn : Any, ErrorOut : Any> Outcome<In, ErrorIn>.fold(
        @BuilderInference success: (In) -> Out,
        @BuilderInference failure: (ErrorIn) -> ErrorOut,
    ): Outcome<Out, ErrorOut> {
        contract {
            callsInPlace(success, AT_MOST_ONCE)
            callsInPlace(failure, AT_MOST_ONCE)
        }

        return when (this@fold) {
            is Outcome.Success<In> -> Outcome.Success(success(value))
            is Outcome.Failure<ErrorIn> -> Outcome.Failure(failure(error))
        }
    }

    @NomadicDsl
    inline fun <In : Any, Out : Any, ErrorIn : Any, ErrorOut : Any> Outcome<In, ErrorIn>.flatFold(
        @BuilderInference success: (In) -> Outcome<Out, ErrorOut>,
        @BuilderInference failure: (ErrorIn) -> Outcome<Out, ErrorOut>,
    ): Outcome<Out, ErrorOut> {
        contract {
            callsInPlace(success, AT_MOST_ONCE)
            callsInPlace(failure, AT_MOST_ONCE)
        }

        return when (this@flatFold) {
            is Outcome.Success<In> -> success(value)
            is Outcome.Failure<ErrorIn> -> failure(error)
        }
    }

}
