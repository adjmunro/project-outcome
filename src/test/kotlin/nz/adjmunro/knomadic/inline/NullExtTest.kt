package nz.adjmunro.knomadic.inline

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NullExtTest {

    @Test
    fun `exists(receiver, block) should execute block if receiver is not null`() {
        // Given
        val receiver: String? = "hello"
        // When
        val result = exists(receiver = receiver, block = { this.uppercase() })
        // Then
        result.shouldBe(expected = "HELLO")
    }

    @Test
    fun `exists(receiver, block) should return null if receiver is null`() {
        // Given
        val receiver: String? = null
        // When
        val result = exists(receiver = receiver, block = { this.uppercase() })
        // Then
        result.shouldBe(expected = null)
    }

    @Test
    fun `T_exists(block) should execute block if receiver is not null`() {
        // Given
        val receiver: String? = "world"
        // When
        val result = receiver.exists(block = { this.reversed() })
        // Then
        result.shouldBe(expected = "dlrow")
    }

    @Test
    fun `T_exists(block) should return null if receiver is null`() {
        // Given
        val receiver: String? = null
        // When
        val result = receiver.exists(block = { this.reversed() })
        // Then
        result.shouldBe(expected = null)
    }

    @Test
    fun `fallback should return value if not null`() {
        // Given
        val receiver: String? = "fallback"
        // When
        val result = receiver.fallback(none = { NullPointerException("should not be called") })
        // Then
        result.shouldBe(expected = "fallback")
    }

    @Test
    fun `fallback should call none if receiver is null`() {
        // Given
        val receiver: String? = null
        // When
        val result = receiver.fallback(none = { "default" })
        // Then
        result.shouldBe(expected = "default")
    }

    @Test
    fun `fallback should throw NullPointerException by default if receiver is null`() {
        // Given
        val receiver: String? = null
        // When/Then
        shouldThrow<NullPointerException> {
            receiver.fallback()
        }
    }
}
