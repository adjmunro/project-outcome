@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package nz.adjmunro.nomadic.error.outcome

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.raise.RaiseScope
import nz.adjmunro.nomadic.error.raise.RaiseScope.Companion.expect
import nz.adjmunro.nomadic.error.util.ThrowableExt.nonFatalOrThrow
import nz.adjmunro.nomadic.error.util.failure
import nz.adjmunro.nomadic.error.util.rethrow
import nz.adjmunro.nomadic.error.util.success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

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
 * outcomeOf(::failure) { this: RaiseScope<Throwable> -> ... }
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
@NomadicDsl
inline fun <Ok : Any, Error : Any> outcomeOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Outcome<Ok, Error> = ::rethrow,
    @BuilderInference crossinline block: RaiseScope<Error>.() -> Ok,
): Outcome<Ok, Error> {
    return RaiseScope.fold(
        block = block,
        catch = catch,
        recover = ::failure,
        transform = ::success,
    )
}

@NomadicDsl
inline fun <Auto : AutoCloseable, Ok : Any, Error : Any> Auto.outcomeOf(
    @BuilderInference crossinline catch: (throwable: Throwable) -> Outcome<Ok, Error> = ::rethrow,
    @BuilderInference crossinline block: RaiseScope<Error>.(Auto) -> Ok,
): Outcome<Ok, Error> {
    // AutoCloseable.use() rethrows everything, avoiding RaiseScope interference.
    return RaiseScope.fold(
        block = { scope -> use { auto -> block(scope, auto) } },
        catch = catch,
        recover = ::failure,
        transform = ::success,
    )
}

@Suppress("UNCHECKED_CAST")
@NomadicDsl
inline fun <Ok : Any, Error : Throwable> tryOutcome(
    @BuilderInference crossinline catch: (throwable: Error) -> Outcome<Ok, Error> = ::failure,
    @BuilderInference crossinline block: () -> Ok,
): Outcome<Ok, Error> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }

    return try {
        Outcome.Success(block())
    } catch (e: Throwable) {
        catch(e.nonFatalOrThrow() as Error)
    }
}

// TODO Maybe can remove this function, on the basis that the other is inline? so this ought to work for suspend too?
//@NomadicDsl
//suspend inline fun <Ok : Any, Error : Any> outcomeOf(
//    @BuilderInference crossinline catch: (throwable: Throwable) -> Outcome<Ok, Error> = ::rethrows,
//    @BuilderInference crossinline block: suspend RaiseScope<Error>.() -> Ok,
//): Outcome<Ok, Error> {
//    return RaiseScope.foldSuspend(
//        block = block,
//        catch = catch,
//        recover = ::failure,
//        transform = ::success,
//    )
//}

fun test() {
    val x = outcomeOf { expect<Throwable>() }
}
