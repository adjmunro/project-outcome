package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.OutcomeFold.flatFold
import nz.adjmunro.nomadic.error.util.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
object OutcomeUtils {

    @NomadicDsl
    fun <Ok : Any, Error : Any> Outcome<Outcome<Ok, Error>, Outcome<Ok, Error>>.flatten(): Outcome<Ok, Error> {
        return flatFold(
            success = ::identity,
            failure = ::identity,
        )
    }

    @NomadicDsl
    fun <Ancestor : Any, Ok : Ancestor, Error : Ancestor> Outcome<Ok, Error>.collapse(): Ancestor {
        return when (this@collapse) {
            is Outcome.Success<Ok> -> value
            is Outcome.Failure<Error> -> error
        }
    }

    @NomadicDsl
    inline fun <Ok : Output, Error : Output, Output : Any> Outcome<Ok, Error>.collapse(
        @BuilderInference success: (Ok) -> Output,
        @BuilderInference failure: (Error) -> Output,
    ): Output {
        contract {
            callsInPlace(success, AT_MOST_ONCE)
            callsInPlace(failure, AT_MOST_ONCE)
        }

        return when (this@collapse) {
            is Outcome.Success<Ok> -> success(value)
            is Outcome.Failure<Error> -> failure(error)
        }
    }

}
