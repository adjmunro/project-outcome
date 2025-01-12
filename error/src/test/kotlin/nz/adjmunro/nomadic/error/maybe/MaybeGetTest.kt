package nz.adjmunro.nomadic.error.maybe

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import nz.adjmunro.nomadic.error.maybe.Maybe.None
import nz.adjmunro.nomadic.error.maybe.Maybe.Some
import nz.adjmunro.nomadic.error.maybe.MaybeGet.getOrDefault
import nz.adjmunro.nomadic.error.maybe.MaybeGet.getOrElse
import nz.adjmunro.nomadic.error.maybe.MaybeGet.getOrNull
import nz.adjmunro.nomadic.error.maybe.MaybeGet.getOrThrow

class MaybeGetTest : BehaviorSpec({
    Given("A Maybe is being accessed") {
        When("it is Some(true)") {
            val maybe: Maybe<Boolean> = Some(true)
            Then("getOrDefault should return true") {
                maybe.getOrDefault(false) shouldBe true
            }
            Then("getOrElse should return true") {
                maybe.getOrElse { false } shouldBe true
            }
            Then("getOrNull should return true") {
                maybe.getOrNull() shouldBe true
            }
            Then("getOrThrow should return true") {
                shouldNotThrow<NullPointerException> {
                    maybe.getOrThrow() shouldBe true
                }
            }
            Then("getOrThrow should return true") {
                shouldNotThrow<IllegalStateException> {
                    maybe.getOrThrow { IllegalStateException() } shouldBe true
                }
            }
        }

        When("it is None") {
            val maybe: Maybe<Boolean> = None
            Then("getOrDefault should return false") {
                maybe.getOrDefault(false) shouldBe false
            }
            Then("getOrElse should return false") {
                maybe.getOrElse { false } shouldBe false
            }
            Then("getOrNull should return null") {
                maybe.getOrNull() shouldBe null
            }
            Then("getOrThrow should throw NullPointerException") {
                shouldThrow<NullPointerException> {
                    maybe.getOrThrow() shouldNotBe true
                }
            }
            Then("getOrThrow should throw IllegalStateException") {
                shouldThrow<IllegalStateException> {
                    maybe.getOrThrow { IllegalStateException() } shouldNotBe true
                }
            }
        }
    }
})
