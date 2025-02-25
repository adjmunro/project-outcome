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

    inline fun <T : Any> Fetch<T>.onCompleted(action: Fetch.Completed<T>.() -> Unit): Fetch<T> {
        if (this is Fetch.Completed) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onCompleted(
        crossinline predicate: (result: T) -> Boolean,
        action: Fetch.Completed<T>.() -> Unit,
    ): Fetch<T> {
        if (this is Fetch.Completed && predicate(result)) action(this)
        return this
    }

    inline fun <T : Any> Fetch<T>.onCompleted(
        predicate: Boolean,
        action: Fetch.Completed<T>.() -> Unit,
    ): Fetch<T> {
        if (this is Fetch.Completed && predicate) action(this)
        return this
    }
}
