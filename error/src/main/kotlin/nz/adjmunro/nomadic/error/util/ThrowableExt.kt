package nz.adjmunro.nomadic.error.util

import nz.adjmunro.nomadic.error.raise.exception.RaiseScopeLeakedException
import java.util.concurrent.CancellationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
object ThrowableExt {

    fun Throwable.isFatal(): Boolean {
        contract {
            returns(false) implies (this@isFatal !is CancellationException)
            returns(false) implies (this@isFatal !is InterruptedException)
            returns(false) implies (this@isFatal !is LinkageError)
            returns(false) implies (this@isFatal !is OutOfMemoryError)
            returns(false) implies (this@isFatal !is RaiseScopeLeakedException)
            returns(false) implies (this@isFatal !is StackOverflowError)
            returns(false) implies (this@isFatal !is ThreadDeath)
            returns(false) implies (this@isFatal !is VirtualMachineError)
        }

        return when (this) {
            is CancellationException -> true
            is InterruptedException -> true
            is LinkageError -> true
            is OutOfMemoryError -> true
            is RaiseScopeLeakedException -> true
            is StackOverflowError -> true
            is ThreadDeath -> true
            is VirtualMachineError -> true
            else -> false
        }
    }

    fun <Error : Throwable> Error.nonFatalOrThrow(): Error {
        return if (isFatal()) throw this else this
    }

}
