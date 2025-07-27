package nz.adjmunro.knomadic.outcome.members

import nz.adjmunro.knomadic.outcome.Failure
import nz.adjmunro.knomadic.outcome.Outcome
import nz.adjmunro.knomadic.outcome.OutcomeDsl
import nz.adjmunro.knomadic.outcome.Success
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * - ***If all*** [outcome][Outcome] in the [Iterable] are [success][Outcome.isSuccess],
 *   `returns` a new [Outcome] success, with a [List] of each element's encapsulated value.
 * - ***If any*** [outcome][Outcome] in the [Iterable] are [failure][Outcome.isFailure],
 *   `returns` a new [Outcome] failure with the result of the [reduce] function.
 */
@OutcomeDsl
public inline fun <Ok : Any, Error : Any> Iterable<Outcome<Ok, Error>>.aggregate(
    reduce: (List<Error>) -> Error,
): Outcome<List<Ok>, Error> {
    contract { callsInPlace(reduce, InvocationKind.AT_MOST_ONCE) }

    val (
        errors: List<Outcome<Ok, Error>>,
        successes: List<Outcome<Ok, Error>>,
    ) = partition(predicate = Outcome<Ok, Error>::isFailure)

    return when {
        errors.isNotEmpty() -> Failure(
            error = reduce(errors.map(transform = Outcome<Ok, Error>::errorOrThrow)),
        )

        else -> Success(
            value = successes.map(transform = Outcome<Ok, Error>::getOrThrow),
        )
    }
}
