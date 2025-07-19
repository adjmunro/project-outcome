package nz.adjmunro.knomadic.inline

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.FlowCollector
import org.junit.jupiter.api.Test

class ReturnExtTest {

    @Test
    fun `stringItself should return string representation of value`() {
        // Given
        val value = 123
        // When
        val result = stringItself(value = value)
        // Then
        result.shouldBe(expected = "123")
    }

    @Test
    fun `stringCaller should return string representation of receiver`() {
        // Given
        val value = 456
        // When
        val result = value.stringCaller()
        // Then
        result.shouldBe(expected = "456")
    }

    @Test
    fun `emptyString should return empty string`() {
        // Given
        val value = "ignored"
        // When
        val result = emptyString(ignore = value)
        // Then
        result.shouldBe(expected = "")
    }

    @Test
    fun `itself should return the value itself`() {
        // Given
        val value = "hello"
        // When
        val result = itself(value = value)
        // Then
        result.shouldBe(expected = value)
    }

    @Test
    fun `caller should return the receiver itself`() {
        // Given
        val value = 42
        // When
        val result = value.caller()
        // Then
        result.shouldBe(expected = value)
    }

    @Test
    fun `rethrow should throw the provided throwable`() {
        // Given
        val throwable = IllegalArgumentException("fail")
        // When/Then
        shouldThrow<IllegalArgumentException> {
            rethrow(throwable = throwable)
        }
    }

    @Test
    fun `rethrow with FlowCollector should throw the provided throwable`() {
        // Given
        val throwable = IllegalStateException("fail")
        val collector = object : FlowCollector<Int> {
            override suspend fun emit(value: Int) { }
        }
        // When/Then
        shouldThrow<IllegalStateException> {
            rethrow(collector = collector, throwable = throwable)
        }
    }

    @Test
    fun `nulls should always return null`() {
        // Given
        val value = "ignored"
        // When
        val result = nulls(ignore = value)
        // Then
        result.shouldBe(expected = null)
    }

    @Test
    fun `unit should always return Unit`() {
        // Given
        val value = "ignored"
        // When
        val result = unit(ignore = value)
        // Then
        result.shouldBe(expected = Unit)
    }
}
