package nz.adjmunro.nomadic.error

import nz.adjmunro.nomadic.error.fallible.Fallible
import nz.adjmunro.nomadic.error.maybe.Maybe
import nz.adjmunro.nomadic.error.outcome.Outcome

/**
 * An interface representing error handling wrappers that aim to resolve issues
 * with [KotlinResult] by representing both [Ok] and [Error] types.
 *
 * Consider using:
 * - [Maybe] when only [Ok] requires instance data;
 * - [Fallible] when only [Error] requires instance data;
 * - [Outcome] when both [Ok] and [Error] require instance data.
 */
interface BinaryResult<out Ok: Any, out Error: Any>
