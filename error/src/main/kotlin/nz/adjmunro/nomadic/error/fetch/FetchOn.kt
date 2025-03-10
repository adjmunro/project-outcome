package nz.adjmunro.nomadic.error.fetch

object FetchOn {
    inline fun <T : Any> Fetch<T>.onNotStarted(action: Fetch.NotStarted.() -> Unit): Fetch<T> {
        if (this is Fetch.NotStarted) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onInProgress(action: Fetch.InProgress.() -> Unit): Fetch<T> {
        if (this is Fetch.InProgress) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onFinished(action: Fetch.Finished<T>.() -> Unit): Fetch<T> {
        if (this is Fetch.Finished) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onFinished(
        crossinline predicate: (result: T) -> Boolean,
        action: Fetch.Finished<T>.() -> Unit,
    ): Fetch<T> {
        if (this is Fetch.Finished && predicate(result)) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onFinished(
        predicate: Boolean,
        action: Fetch.Finished<T>.() -> Unit,
    ): Fetch<T> {
        if (this is Fetch.Finished && predicate) action(this)
        return this
    }
}
