package com.yandex.div.core

import java.io.Closeable

/**
 * An object that may hold resources (such as file or socket handles) until it is closed.
 * In comparison with [Closeable] IOException is dropped from the [close] method.
 */
fun interface Disposable : AutoCloseable, Closeable {
    /**
     * Closes this resource, relinquishing any underlying resources.
     *
     * Implementation of this method must be idempotent.
     */
    override fun close()

    companion object {

        @JvmField
        val NULL: Disposable = Disposable { }
    }
}
