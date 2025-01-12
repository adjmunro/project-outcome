package nz.adjmunro.nomadic.error.maybe

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import nz.adjmunro.nomadic.error.maybe.MaybeIs.isNone
import nz.adjmunro.nomadic.error.maybe.MaybeIs.isSome

class MaybeIsTest : BehaviorSpec({
    Given("A Maybe is being checked") {
        When("it is Some(true)") {
            val maybe: Maybe<Boolean> = Maybe.Some(true)
            Then("isSome should return true") {
                maybe.isSome() shouldBe true
                if (maybe.isSome()) {
                    maybe.value shouldBe true
                }
            }
            Then("isNone should return false") {
                maybe.isNone() shouldBe false
            }

            and("predicate is true") {
                val predicate = true
                Then("isSome should return true") {
                    maybe isSome { predicate } shouldBe true
                }
                Then("isNone should return false") {
                    maybe isNone { predicate } shouldBe false
                }
            }

            and("predicate is false") {
                val predicate = false
                Then("isSome should return false") {
                    maybe isSome { predicate } shouldBe false
                }
                Then("isNone should return false") {
                    maybe isNone { predicate } shouldBe false
                }
            }
        }

        When("it is None") {
            val maybe: Maybe<Boolean> = Maybe.None
            Then("isSome should return false") {
                maybe.isSome() shouldBe false
            }
            Then("isNone should return true") {
                maybe.isNone() shouldBe true
            }

            and("predicate is true") {
                val predicate = true
                Then("isSome should return false") {
                    maybe isSome { predicate } shouldBe false
                }
                Then("isNone should return true") {
                    maybe isNone { predicate } shouldBe true
                }
            }

            and("predicate is false") {
                val predicate = false
                Then("isSome should return false") {
                    maybe isSome { predicate } shouldBe false
                }
                Then("isNone should return false") {
                    maybe isNone { predicate } shouldBe false
                }
            }
        }
    }
})
