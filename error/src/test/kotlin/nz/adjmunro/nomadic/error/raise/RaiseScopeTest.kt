package nz.adjmunro.nomadic.error.raise

import kotlinx.coroutines.runBlocking
import nz.adjmunro.nomadic.error.outcome.Outcome.Companion.outcomeOf
import nz.adjmunro.nomadic.error.outcome.Outcome.Failure
import nz.adjmunro.nomadic.error.outcome.OutcomeAs.asOutcome
import nz.adjmunro.nomadic.error.outcome.OutcomeGet.getOrThrow
import nz.adjmunro.nomadic.error.outcome.flatMapSuccess
import nz.adjmunro.nomadic.error.outcome.flatten
import nz.adjmunro.nomadic.error.outcome.fold
import nz.adjmunro.nomadic.error.outcome.mapSuccess
import nz.adjmunro.nomadic.error.outcome.recover
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.raise

class RaiseScopeTest {

    fun test() = runBlocking {
        { 1 }.asOutcome().also {

        }

        outcomeOf {
            raise { "error" }
            1
        }.mapSuccess {
            // TODO 1) should flatmap map to both or just same state?
            // TODO 2) should transform functions provide a RaiseScope?
            it.toDouble().asOutcome().getOrThrow() // throws!
        }.also {

        }

        outcomeOf {
            raise { "error" }
            1
        }.flatMapSuccess {
            it.toDouble().asOutcome().recover { 0.0 }
        }.also {

        }

        outcomeOf {
            raise { "error" }
            1
        }.fold(
            success = {
//                raise { 1 }
//                outcomeOf(::Failure) { it.toDouble() }
            },
            failure = {
//                raise { 1 }
                "0.0"
                outcomeOf(::Failure) { "" }
            },
//            catch = { 1 },
        ).also {

        }.flatten.also {

        }

    }

}
