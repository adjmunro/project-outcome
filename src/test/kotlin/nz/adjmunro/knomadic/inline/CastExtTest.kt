package nz.adjmunro.knomadic.inline

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CastExtTest {

    @Test
    fun `castOrThrow should cast when type matches, throw otherwise`() {
        // Given
        val value: Any = "string"
        // When
        val result = value.castOrThrow<String>()
        // Then
        result.shouldBe(expected = "string")

        // When/Then - throws
        val intValue: Any = 42
        shouldThrow<ClassCastException> {
            intValue.castOrThrow<String>()
        }
    }

    @Test
    fun `castOrNull should cast when type matches, return null otherwise`() {
        // Given
        val value: Any? = "string"
        // When
        val result = value.castOrNull<String>()
        // Then
        result.shouldBe(expected = "string")

        // When
        val intValue: Any? = 42
        val nullResult = intValue.castOrNull<String>()
        // Then
        nullResult.shouldBe(expected = null)
    }

    @Test
    fun `castOrElse should cast or return default`() {
        // Given
        val value: Any? = "string"
        // When
        val result = value.castOrElse(default = { "default" })
        // Then
        result.shouldBe(expected = "string")

        // When
        val intValue: Any? = 42
        val elseResult = intValue.castOrElse(default = { "default" })
        // Then
        elseResult.shouldBe(expected = "default")
    }

    @Test
    fun `instanceOf should return true if instance matches, false otherwise`() {
        // Given
        val value: Any? = "string"
        // When
        val result = value.instanceOf<String>()
        // Then
        result.shouldBe(expected = true)

        // When
        val intValue: Any? = 42
        val falseResult = intValue.instanceOf<String>()
        // Then
        falseResult.shouldBe(expected = false)
    }

    @Test
    fun `on should execute block if instance matches and return original`() {
        // Given
        val value: Any = "string"
        var called = false
        // When
        val result = value.on(instanceof = String::class, block = { called = true })
        // Then
        called.shouldBe(expected = true)
        result.shouldBe(expected = value)

        // When
        called = false
        val intValue: Any = 42
        val intResult = intValue.on(instanceof = String::class, block = { called = true })
        // Then
        called.shouldBe(expected = false)
        intResult.shouldBe(expected = intValue)
    }
}
