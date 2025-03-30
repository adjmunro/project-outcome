package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KnomadicDsl
import nz.adjmunro.knomadic.KotlinResult
import nz.adjmunro.knomadic.outcome.Outcome.Failure
import nz.adjmunro.knomadic.outcome.Outcome.Success
import nz.adjmunro.knomadic.raise.RaiseScope
import nz.adjmunro.knomadic.util.rethrow

/**
 * Context runner that encapsulates the [Ok] result of [block] as an [Outcome.Success], and any
 * [raised][RaiseScope.raised] or [caught][RaiseScope.catch] [errors][Error] as an [Outcome.Failure].
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
 *     catch({ it }) { throw NullPointerException() }
 * }
 * ```
 *
 * @param catch Map thrown exceptions to an [Outcome]. (Throws by default).
 * @param block The code to execute.
 */
@KnomadicDsl
public inline fun <Ok : Any, Error : Any> outcomeOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Outcome<Ok, Error> = ::rethrow,
    @BuilderInference crossinline block: RaiseScope<Error>.() -> Ok,
): Outcome<Ok, Error> {
    return RaiseScope.Companion.fold(
        block = block,
        catch = catch,
        recover = ::Failure,
        transform = ::Success,
    )
}

/**
 * Represents either a [Success] or [Failure] state.
 *
 * - Unlike [KotlinResult], [Outcome] stores the [Error] type explicitly to prevent information loss.
 * - Both [Ok] and [Error] are restricted to non-null types.
 *
 * @property Ok The type of a successful result.
 * @property Error The type of an error result.
 * @see Outcome.Success
 * @see Outcome.Failure
 * @see outcomeOf
 */
public sealed interface Outcome<out Ok : Any, out Error : Any> {

    public operator fun component1(): Ok? = getOrNull()
    public operator fun component2(): Error? = errorOrNull()

    /**
     * A successful [Outcome].
     *
     * @property value The successful result.
     */
    @JvmInline
    public value class Success<out Ok : Any>(public val value: Ok) : Outcome<Ok, Nothing> {
        override fun component1(): Ok = value
        override fun toString(): String = "Outcome::Success<${value::class.simpleName}>($value)"
    }

    /**
     * A failed [Outcome].
     *
     * @property error The error result.
     */
    @JvmInline
    public value class Failure<out Error : Any>(public val error: Error) : Outcome<Nothing, Error> {
        override fun component2(): Error = error
        override fun toString(): String = "Outcome::Failure<${error::class.simpleName}>($error)"
    }
    
}
