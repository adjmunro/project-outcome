package nz.adjmunro.knomadic.outcome

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.result.shouldBeSuccess
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.catch
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.default
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.ensure
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.ensureNotNull
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.fold
import nz.adjmunro.knomadic.raise.RaiseScope.Companion.raise
import nz.adjmunro.knomadic.util.collect
import nz.adjmunro.knomadic.util.exceptionOrElse
import nz.adjmunro.knomadic.util.flatMap
import nz.adjmunro.knomadic.util.mapFailure
import nz.adjmunro.knomadic.util.nullable
import nz.adjmunro.knomadic.util.nullfold
import nz.adjmunro.knomadic.util.resultOf
import nz.adjmunro.knomadic.util.throwfold
import kotlin.Result.Companion.success
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * This "Test" class exists to validate via the compiler that each function accepts both
 * suspend and blocking usage. We don't actually need to run these tests, since their real
 * purpose is to cause build errors if the function signatures are incorrect.
 */
@Ignore("Exists to validate via the compiler that each function accepts both suspend and blocking usage")
@Suppress("UnreachableCode")
class SuspendTest {

    private val blocking = { true }
    private val blockingException = { IllegalStateException() }
    private val suspend = suspend { true }
    private val suspendException = suspend { IllegalStateException() }

    @Test
    fun `KotlinResult works with suspend`(): TestResult = runTest {
        // Given
        val result = resultOf { suspend() }
        val nullable = nullable { suspend() }
        val list = listOf(resultOf { 1 }, resultOf { 2 }, resultOf { 3 })

        // When
        result.exceptionOrElse { suspendException() }
        result.flatMap { suspend().let(::success) }
        result.mapFailure { suspendException() }
        list.collect { suspendException() }

        // Then
        result.shouldBeSuccess { it.shouldBeEqual(true) }
        nullable.shouldNotBeNull().shouldBeEqual(true)
    }

    @Test
    fun `KotlinResult works without suspend`() {
        // Given
        val result = resultOf { blocking() }
        val nullable = nullable { blocking() }
        val list = listOf(resultOf { 1 }, resultOf { 2 }, resultOf { 3 })

        // When
        result.exceptionOrElse { blockingException() }
        result.flatMap { blocking().let(::success) }
        result.mapFailure { blockingException() }
        list.collect { blockingException() }

        // Then
        result.shouldBeSuccess { it.shouldBeEqual(true) }
        nullable.shouldNotBeNull().shouldBeEqual(true)
    }

    @Test
    fun `InlineUtils works with suspend`(): TestResult = runTest {
        // Given
        val nullIf = nullfold( { suspend() }, { suspend() })
        val throwIf = throwfold( { suspend() }, { suspend() })

        // Then
        nullIf.shouldNotBeNull().shouldBeEqual(true)
        throwIf.shouldNotBeNull().shouldBeEqual(true)
    }

    @Test
    fun `InlineUtils works without suspend`() {
        // Given
        val nullIf = nullfold( { blocking() }, { blocking() })
        val throwIf = throwfold( { blocking() }, { blocking() })

        // Then
        nullIf.shouldNotBeNull().shouldBeEqual(true)
        throwIf.shouldNotBeNull().shouldBeEqual(true)
    }

    @Test
    fun `RaiseScope works with suspend`(): TestResult = runTest {
        RaiseScope.default {
            raised(suspend())
            raise { suspend() }
            catch { suspend() }
            catch({ suspend() }) { suspend() }
            ensure(true) { suspend() }
            ensureNotNull(true) { suspend() }

            fold<Boolean, Boolean, Boolean>(
                block = { suspend() },
                catch = { suspend() },
                recover = { suspend() },
                transform = { suspend() }
            )
        }
    }

    @Test
    fun `RaiseScope works without suspend`() {
        RaiseScope.default {
            raised(blocking())
            raise { blocking() }
            catch { blocking() }
            catch({ blocking() }) { blocking() }
            ensure(true) { blocking() }
            ensureNotNull(true) { blocking() }

            fold<Boolean, Boolean, Boolean>(
                block = { blocking() },
                catch = { blocking() },
                recover = { blocking() },
                transform = { blocking() }
            )
        }
    }

    @Test
    fun `Outcome works with suspend`(): TestResult = runTest {
        val outcome = outcomeOf(::failureOf) { suspend() }
        outcomeOf<Boolean, Boolean>({ failureOf(suspend()) }) { suspend() }
        maybeOf { suspend() }
        maybeOf<Boolean>({ suspend(); failureOf(Unit) }) { suspend() }
        faultyOf<Boolean> { suspend() }
        faultyOf<Boolean>({ failureOf(suspend()) }) { suspend() }

        outcome.getOrElse { suspend() }
        outcome.errorOrElse { suspend() }

        outcome.isSuccess { suspend() }
        outcome.isFailure { suspend() }

        outcome.onSuccess { suspend() }
        outcome.onFailure { suspend() }

        outcome.map({ suspend() }, { suspend() })
        outcome.mapSuccess { suspend() }
        outcome.mapFailure { suspend() }

        outcome.flatMapSuccess { suspend().let(::successOf) }
        outcome.flatMapFailure { suspend().let(::failureOf) }
        outcome.fold({ suspend() }, { suspend() })

        outcome.recover { suspend() }
        outcome.falter { suspend() }
        outcome.coerceToSuccess { suspend() }
        outcome.coerceToFailure { suspend() }
    }

    @Test
    fun `Outcome works without suspend`() {
        // Given
        val outcome = outcomeOf(::failureOf) { blocking() }
        outcomeOf<Boolean, Boolean>({ failureOf(blocking()) }) { blocking() }
        maybeOf { blocking() }
        maybeOf<Boolean>({ blocking(); failureOf(Unit) }) { blocking() }
        faultyOf<Boolean> { blocking() }
        faultyOf<Boolean>({ failureOf(blocking()) }) { blocking() }

        // When
        outcome.getOrElse { blocking() }
        outcome.errorOrElse { blocking() }

        outcome.isSuccess { blocking() }
        outcome.isFailure { blocking() }

        outcome.onSuccess { blocking() }
        outcome.onFailure { blocking() }

        outcome.map({ blocking() }, { blocking() })
        outcome.mapSuccess { blocking() }
        outcome.mapFailure { blocking() }

        outcome.flatMapSuccess { blocking().let(::successOf) }
        outcome.flatMapFailure { blocking().let(::failureOf) }
        outcome.fold({ blocking() }, { blocking() })

        outcome.recover { blocking() }
        outcome.falter { blocking() }
        outcome.coerceToSuccess { blocking() }
        outcome.coerceToFailure { blocking() }
    }

    @Test
    fun `OutcomeFlow works with suspend`(): TestResult = runTest {
        // Given
        val outcomeFlow = flow { emit(outcomeOf(::failureOf) { suspend() }) }

        // When
        outcomeFlow.onEachSuccess { suspend() }
        outcomeFlow.onEachFailure { suspend() }

        outcomeFlow.mapEachSuccess { suspend() }
        outcomeFlow.mapEachFailure { suspend() }
    }

    @Test
    fun `OutcomeFlow works without suspend`() {
        // Given
        val outcomeFlow = flow { emit(outcomeOf(::failureOf) { blocking() }) }

        // When
        outcomeFlow.onEachSuccess { blocking() }
        outcomeFlow.onEachFailure { blocking() }

        outcomeFlow.mapEachSuccess { blocking() }
        outcomeFlow.mapEachFailure { blocking() }
    }
}
