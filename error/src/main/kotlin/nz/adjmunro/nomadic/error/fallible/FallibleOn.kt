package nz.adjmunro.nomadic.error.fallible

import nz.adjmunro.nomadic.error.NomadicDsl
import nz.adjmunro.nomadic.error.fallible.FallibleIs.isOops
import nz.adjmunro.nomadic.error.fallible.FallibleIs.isPass
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
object FallibleOn {

    @NomadicDsl
    inline infix fun <Error : Any> Fallible<Error>.onPass(
        @BuilderInference block: () -> Unit,
    ): Fallible<Error> {
        contract { callsInPlace(block, AT_MOST_ONCE) }

        if (isPass()) block()
        return this@onPass
    }

    @NomadicDsl
    inline infix fun <Error : Any> Fallible<Error>.onOops(
        @BuilderInference block: (Error) -> Unit,
    ): Fallible<Error> {
        contract { callsInPlace(block, AT_MOST_ONCE) }

        if (isOops()) block(error)
        return this@onOops
    }

}
