package nz.adjmunro.knomadic.inline

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FoldExtTest {
    @Test
    fun `fold should call truthy or falsy based on predicate`() {
        // Given
        val value = 10
        // When
        val resultTruthy = value.fold(
            predicate = { this > 5 },
            falsy = { -1 },
            truthy = { 1 }
        )
        // Then
        resultTruthy.shouldBe(expected = 1)

        // When
        val resultFalsy = value.fold(
            predicate = { this < 5 },
            falsy = { -1 },
            truthy = { 1 }
        )
        // Then
        resultFalsy.shouldBe(expected = -1)
    }

    @Test
    fun `flatmap should call truthy or falsy based on predicate`() {
        // Given
        val value = 10
        // When
        val resultTruthy = value.flatmap(
            predicate = { this > 5 },
            truthy = { this * 2 }
        )
        // Then
        resultTruthy.shouldBe(expected = 20)

        // When
        val resultFalsy = value.flatmap(
            predicate = { this < 5 },
            falsy = { this * 3 }
        )
        // Then
        resultFalsy.shouldBe(expected = 30)

        // When - default returns itself
        val resultDefault = value.flatmap(predicate = { false })
        // Then
        resultDefault.shouldBe(expected = value)
    }

    @Test
    fun `nullfold should call some if not null, none if null`() {
        // Given
        val value: String? = "hello"
        // When
        val resultSome = value.nullfold(
            none = { "none" },
            some = { it.uppercase() }
        )
        // Then
        resultSome.shouldBe(expected = "HELLO")

        // When
        val nullValue: String? = null
        val resultNone = nullValue.nullfold(
            none = { "none" },
            some = { it.uppercase() }
        )
        // Then
        resultNone.shouldBe(expected = "none")
    }

    @Test
    fun `nullfold should pass NullPointerException to none`() {
        // Given
        val nullValue: String? = null
        // When
        val result = nullValue.nullfold(
            none = { it.message ?: "no message" },
            some = { it }
        )
        // Then
        result.shouldBe(expected = "Nullfold source was null.")
    }

    @Test
    fun `throwfold should call throws if Throwable, pass otherwise`() {
        // Given
        val value: Any = IllegalStateException("fail")
        // When
        val resultThrows = value.throwfold(
            throws = { it.message ?: "no msg" },
            pass = { "ok" }
        )
        // Then
        resultThrows.shouldBe(expected = "fail")

        // When
        val okValue: Any = "ok"
        val resultPass = okValue.throwfold(
            throws = { "fail" },
            pass = { it as String }
        )
        // Then
        resultPass.shouldBe(expected = "ok")
    }
}
