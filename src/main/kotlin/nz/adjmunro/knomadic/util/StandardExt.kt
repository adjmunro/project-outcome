package nz.adjmunro.knomadic.util

public inline fun <T> T.alsoIf(predicate: Boolean, block: (T) -> Unit): T =
    also { if (predicate) block(it) }

public inline fun <T> T.alsoIf(predicate: (T) -> Boolean, block: (T) -> Unit): T =
    also { if (predicate(it)) block(it) }

public inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T =
    apply { if (predicate) block(this) }

public inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> Unit): T =
    apply { if (predicate(this)) block(this) }

public inline fun <T> T.letIf(predicate: Boolean, block: (T) -> T): T =
    let { if (predicate) block(it) else it }

public inline fun <T> T.letIf(predicate: (T) -> Boolean, block: (T) -> T): T =
    let { if (predicate(it)) block(it) else it }

public inline fun <T> T.runIf(predicate: Boolean, block: T.() -> T): T =
    run { if (predicate) block(this) else this }

public inline fun <T> T.runIf(predicate: T.() -> Boolean, block: T.() -> T): T =
    run { if (predicate(this)) block(this) else this }

public inline fun <T> runIf(predicate: Boolean, block: () -> T): T? {
    return if (predicate) run(block) else null
}

public inline fun <T> runIf(predicate: () -> Boolean, block: () -> T): T? {
    return if (predicate()) block() else null
}
