package nz.adjmunro.nomadic.error.fallible

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import nz.adjmunro.nomadic.error.util.pass

object FallibleFoldTest : BehaviorSpec({
    Given("A fallible") {
        When("it is a success") {
            Then("it should return the success value") {
                // pass() shouldBe pass()
            }
        }
    }
})
