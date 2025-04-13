package nz.adjmunro.knomadic.fetch

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import nz.adjmunro.knomadic.fetch.flow.fetchOrDefault
import nz.adjmunro.knomadic.fetch.flow.fetchOrElse
import nz.adjmunro.knomadic.fetch.flow.fetchOrNull
import nz.adjmunro.knomadic.fetch.flow.fetchOrThrow
import nz.adjmunro.knomadic.fetch.flow.fetchUnwrap
import nz.adjmunro.knomadic.fetch.flow.filterOnlyFinished
import nz.adjmunro.knomadic.fetch.flow.flatMapFetching
import nz.adjmunro.knomadic.fetch.flow.flatMapFinished
import nz.adjmunro.knomadic.fetch.flow.flatMapInitial
import nz.adjmunro.knomadic.fetch.flow.flatten
import nz.adjmunro.knomadic.fetch.flow.fold
import nz.adjmunro.knomadic.fetch.flow.mapFetchingToFinished
import nz.adjmunro.knomadic.fetch.flow.mapFinished
import nz.adjmunro.knomadic.fetch.flow.mapInitialToFinished
import nz.adjmunro.knomadic.fetch.flow.mapToFinished
import nz.adjmunro.knomadic.fetch.flow.onEachFetch
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
    private val suspend = suspend { true }

    @Test @Suppress("UnreachableCode")
    fun `fetch works with suspend`(): TestResult = runTest {
        // Given
        val fetch: Fetch<Boolean> = Fetch.Initial

        // When
        fetch.getOrThrow()
        fetch.getOrDefault(true)
        fetch.getOrElse { suspend() }
        fetch.getOrNull()
        fetch.unwrap { suspend() }
        
        fetch.isStarted
        fetch.isNotStarted
        fetch.isFetching
        fetch.isNotFetching
        fetch.isNotFinished
        fetch.isFinished()
        fetch.isFinished { suspend() }
        
        fetch.onInitial { suspend() }
        fetch.onFetching { suspend() }
        fetch.onFinished { suspend() }
        fetch.onFinished(suspend()) { suspend() }
        fetch.onFinished({ suspend() }) { suspend() }

        fetch.fold({ suspend() }, { suspend() }, { suspend() })
        fetch.mapFinished { suspend() }
        fetch.mapInitialToFinished { suspend() }
        fetch.mapFetchingToFinished { suspend() }
        fetch.mapToFinished({ suspend() }, { suspend() }, { suspend() })

        fetch.flatMapInitial { Fetch.Finished(suspend()) }
        fetch.flatMapFetching { Fetch.Finished(suspend()) }
        fetch.flatMapFinished { Fetch.Finished(suspend()) }
        Fetch.Finished(fetch).flatten
    }

    @Test @Suppress("UnreachableCode")
    fun `fetch works without suspend`() {
        // Given
        val fetch: Fetch<Boolean> = Fetch.Initial

        // When
        fetch.getOrThrow()
        fetch.getOrDefault(true)
        fetch.getOrElse { blocking() }
        fetch.getOrNull()
        fetch.unwrap { blocking() }

        fetch.isStarted
        fetch.isNotStarted
        fetch.isFetching
        fetch.isNotFetching
        fetch.isNotFinished
        fetch.isFinished()
        fetch.isFinished { blocking() }

        fetch.onInitial { blocking() }
        fetch.onFetching { blocking() }
        fetch.onFinished { blocking() }
        fetch.onFinished(blocking()) { blocking() }
        fetch.onFinished({ blocking() }) { blocking() }

        fetch.fold({ blocking() }, { blocking() }, { blocking() })
        fetch.mapFinished { blocking() }
        fetch.mapInitialToFinished { blocking() }
        fetch.mapFetchingToFinished { blocking() }
        fetch.mapToFinished({ blocking() }, { blocking() }, { blocking() })

        fetch.flatMapInitial { Fetch.Finished(blocking()) }
        fetch.flatMapFetching { Fetch.Finished(blocking()) }
        fetch.flatMapFinished { Fetch.Finished(blocking()) }
        Fetch.Finished(fetch).flatten
    }
    
    @Test @Suppress("UnreachableCode")
    fun `fetch flow works with suspend`(): TestResult = runTest {
        // Given
        val fetch = fetch { suspend() }

        // When
        fetch.fetchOrThrow()
        fetch.fetchOrDefault(true)
        fetch.fetchOrElse { suspend() }
        fetch.fetchOrNull()
        fetch.fetchUnwrap { suspend() }
        
        fetch.filterOnlyFinished()
        fetch.onEachFetch({ suspend() }, { suspend() }, { suspend() })

        fetch.fold({ suspend() }, { suspend() }, { suspend() })
        fetch.mapFinished { suspend() }
        fetch.mapInitialToFinished { suspend() }
        fetch.mapFetchingToFinished { suspend() }
        fetch.mapToFinished({ suspend() }, { suspend() }, { suspend() })

        fetch.flatMapInitial { Fetch.Finished(suspend()) }
        fetch.flatMapFetching { Fetch.Finished(suspend()) }
        fetch.flatMapFinished { Fetch.Finished(suspend()) }
        fetch.map { Fetch.Finished(it) }.flatten()
    }

    @Test @Suppress("UnreachableCode")
    fun `fetch flow works with without suspend`(): TestResult = runTest {
        // Given
        val fetch = fetch { blocking() }

        // When
        fetch.fetchOrThrow()
        fetch.fetchOrDefault(true)
        fetch.fetchOrElse { blocking() }
        fetch.fetchOrNull()
        fetch.fetchUnwrap { blocking() }

        fetch.filterOnlyFinished()
        fetch.onEachFetch({ blocking() }, { blocking() }, { blocking() })

        fetch.fold({ blocking() }, { blocking() }, { blocking() })
        fetch.mapFinished { blocking() }
        fetch.mapInitialToFinished { blocking() }
        fetch.mapFetchingToFinished { blocking() }
        fetch.mapToFinished({ blocking() }, { blocking() }, { blocking() })

        fetch.flatMapInitial { Fetch.Finished(blocking()) }
        fetch.flatMapFetching { Fetch.Finished(blocking()) }
        fetch.flatMapFinished { Fetch.Finished(blocking()) }
        fetch.map { Fetch.Finished(it) }.flatten()
    }

}
