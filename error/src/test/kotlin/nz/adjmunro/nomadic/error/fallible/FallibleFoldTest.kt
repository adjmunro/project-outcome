package nz.adjmunro.nomadic.error.fallible

import io.kotest.core.spec.style.BehaviorSpec
import nz.adjmunro.nomadic.error.outcome.Outcome.Companion.outcomeOf
import nz.adjmunro.nomadic.error.outcome.Outcome.Failure

object FallibleFoldTest : BehaviorSpec({
    Given("A fallible") {
        When("it is a success") {
            Then("it should return the success value") {
                // pass() shouldBe pass()
            }
        }
    }
})
