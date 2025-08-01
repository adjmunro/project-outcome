package nz.adjmunro.knomadic.outcome

import nz.adjmunro.knomadic.KotlinResult
import nz.adjmunro.knomadic.outcome.members.errorOrNull
import nz.adjmunro.knomadic.outcome.members.getOrNull
import nz.adjmunro.knomadic.outcome.members.outcomeOf

/**
 * Represents either a [Success] or [Failure] state.
 *
 * - Unlike [KotlinResult], [Outcome] stores the [Error] type explicitly to prevent information loss.
 * - Both [Ok] and [Error] are restricted to non-null types.
 *
 * @property Ok The type of a successful result.
 * @property Error The type of an error result.
 * @see Success
 * @see Failure
 * @see outcomeOf
 */
@OutcomeDsl
public sealed interface Outcome<out Ok : Any, out Error : Any> {
    public operator fun component1(): Ok? = getOrNull()
    public operator fun component2(): Error? = errorOrNull()
}
