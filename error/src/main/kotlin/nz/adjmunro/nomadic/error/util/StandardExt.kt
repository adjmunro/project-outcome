package nz.adjmunro.nomadic.error.util

object StandardExt {

    inline fun <T> T.alsoIf(predicate: Boolean, block: (T) -> Unit): T =
        also { if (predicate) block(it) }

    inline fun <T> T.alsoIf(predicate: (T) -> Boolean, block: (T) -> Unit): T =
        also { if (predicate(it)) block(it) }

    inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T =
        apply { if (predicate) block(this) }

    inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> Unit): T =
        apply { if (predicate(this)) block(this) }

    inline fun <T> T.letIf(predicate: Boolean, block: (T) -> T): T =
        let { if (predicate) block(it) else it }

    inline fun <T> T.letIf(predicate: (T) -> Boolean, block: (T) -> T): T =
        let { if (predicate(it)) block(it) else it }

    inline fun <T> T.runIf(predicate: Boolean, block: T.() -> T): T =
        run { if (predicate) block(this) else this }

    inline fun <T> T.runIf(predicate: T.() -> Boolean, block: T.() -> T): T =
        run { if (predicate(this)) block(this) else this }

    inline fun <T> runIf(predicate: Boolean, block: () -> T): T? {
        return if (predicate) run(block) else null
    }

    inline fun <T> runIf(predicate: () -> Boolean, block: () -> T): T? {
        return if (predicate()) block() else null
    }

}
