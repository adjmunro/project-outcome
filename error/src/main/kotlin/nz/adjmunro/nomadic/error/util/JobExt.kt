package nz.adjmunro.nomadic.error.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object JobExt {

    /**
     * @see awaitAll
     */
    suspend fun CoroutineScope.launchAll(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        vararg jobs: suspend CoroutineScope.() -> Unit,
    ): List<Job> = supervisorScope {
        jobs.map { job -> launch(context, start, job) }
    }

}
