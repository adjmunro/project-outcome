package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.BinaryResult
import nz.adjmunro.nomadic.error.KotlinResult
import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.outcome.Outcome.Failure
import nz.adjmunro.nomadic.error.outcome.OutcomeGet.getOrNull
import nz.adjmunro.nomadic.error.outcome.OutcomeGetError.errorOrNull
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.expect
import nz.adjmunro.nomadic.error.util.outcomeFailure
import nz.adjmunro.nomadic.error.util.rethrow
import nz.adjmunro.nomadic.error.util.outcomeSuccess
import kotlin.experimental.ExperimentalTypeInference

/**
 * A [BinaryResult] that represents either a [Success] or [Failure] state.
 *
 * - Unlike [KotlinResult], [Outcome] stores the [Error] type explicitly to prevent information loss.
 * - Both [Ok] and [Error] are children of [Any], to allow any non-null type to be used.
 *
 * @property Ok The type of a successful result.
 * @property Error The type of an error result.
 * @see Outcome.Success
 * @see Outcome.Failure
 * @see Outcome.outcomeOf
 */
sealed interface Outcome<out Ok : Any, out Error : Any> : BinaryResult<Ok, Error> {

    operator fun component1(): Ok? = getOrNull()
    operator fun component2(): Error? = errorOrNull()

    /**
     * A successful [Outcome].
     *
     * @property value The successful result.
     */
    @JvmInline
    value class Success<out Ok : Any>(val value: Ok) : Outcome<Ok, Nothing> {
        override fun component1(): Ok = value
        override fun toString(): String = "Outcome::Success<${value::class.simpleName}>($value)"
    }

    /**
     * A failed [Outcome].
     *
     * @property error The error result.
     */
    @JvmInline
    value class Failure<out Error : Any>(val error: Error) : Outcome<Nothing, Error> {
        override fun component2(): Error = error
        override fun toString(): String = "Outcome::Failure<${error::class.simpleName}>($error)"
    }

    companion object {
        /**
         * Context runner that encapsulates the [Ok] result of [block] as an [Outcome.Success], and any
         * [raised][RaiseScope.raised] or [expected][RaiseScope.expect] [errors][Error] as an [Outcome.Failure].
         *
         * > ***Note:** [catch] will [rethrow] by default. This is because the consumer needs to manually
         * > override the parameter and map it to an [Outcome] (if desired). Assigning it to
         * > [Failure][Outcome.Failure] directly will only force [Error] to be interpreted as [Throwable]
         * > by the [RaiseScope], which may interfere with the intended [Error] type!*
         * ```kotlin
         * // Outcome<Unit, Throwable>
         * outcomeOf(::Failure) { this: RaiseScope<Throwable> -> ... }
         *
         * // Outcome<Int, String>
         * outcomeOf { this: RaiseScope<String> ->
         *     raise { "error" }
         *     return 3
         * }
         *
         * // Outcome<String, NullPointerException>
         * outcomeOf { this: RaiseScope<NullPointerException> ->
         *     expect<NullPointerException>()
         * }
         * ```
         *
         * @param catch Map thrown exceptions to an [Outcome]. (Throws by default).
         * @param block The code to execute.
         */
        @OptIn(ExperimentalTypeInference::class)
        @NomadicDsl
        inline fun <Ok : Any, Error : Any> outcomeOf(
            @BuilderInference crossinline catch: (throwable: Throwable) -> Outcome<Ok, Error> = ::rethrow,
            @BuilderInference crossinline block: RaiseScope<Error>.() -> Ok,
        ): Outcome<Ok, Error> {
            return RaiseScope.fold(
                block = block,
                catch = catch,
                recover = ::Failure,
                transform = ::Success,
            )
        }
    }
}
