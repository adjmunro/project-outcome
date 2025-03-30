package nz.adjmunro.knomadic.outcome

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class OutcomeTest {

    @Test
    fun `create outcome success`() {
        // Given
        val outcome = Outcome.Success("Success")

        // Then
        outcome.shouldBeInstanceOf<Outcome.Success<String>> { it.value.shouldBeEqual("Success") }
    }

}
