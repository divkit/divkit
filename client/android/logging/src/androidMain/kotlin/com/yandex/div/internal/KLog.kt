package com.yandex.div.internal

import com.yandex.div.logging.Severity

/**
 * Kotlin wrapper for [Log] using inline [Log.isEnabled] checks.
 */
public object KLog {

    private val listeners = mutableListOf<LogListener>()

    public inline fun d(tag: String, message: () -> String) {
        if (isAtLeast(Severity.DEBUG)) {
            print(android.util.Log.DEBUG, tag, message())
        }
    }

    public inline fun d(tag: String, th: Throwable, message: () -> String) {
        if (isAtLeast(Severity.DEBUG)) {
            android.util.Log.d(tag, message(), th)
        }
    }

    public inline fun w(tag: String, message: () -> String) {
        if (isAtLeast(Severity.WARNING)) {
            print(android.util.Log.WARN, tag, message())
        }
    }

    public inline fun w(tag: String, th: Throwable, message: () -> String) {
        if (isAtLeast(Severity.WARNING)) {
            android.util.Log.w(tag, message(), th)
        }
    }

    public inline fun i(tag: String, message: () -> String) {
        if (isAtLeast(Severity.INFO)) {
            print(android.util.Log.INFO, tag, message())
        }
    }

    public inline fun i(tag: String, th: Throwable, message: () -> String) {
        if (isAtLeast(Severity.INFO)) {
            android.util.Log.i(tag, message(), th)
        }
    }

    public inline fun e(tag: String, message: () -> String) {
        if (isAtLeast(Severity.ERROR)) {
            print(android.util.Log.ERROR, tag, message())
        }
    }

    public inline fun e(tag: String, th: Throwable?, message: () -> String = { "" }) {
        if (isAtLeast(Severity.ERROR)) {
            android.util.Log.e(tag, message(), th)
        }
    }

    public fun addListener(listener: LogListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    public fun removeListener(listener: LogListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    @PublishedApi
    internal fun isAtLeast(minLevel: Severity): Boolean = Log.isAtLeast(minLevel)

    @PublishedApi
    internal fun print(priority: Int, tag: String, message: String) {
        android.util.Log.println(priority, tag, message)
        synchronized(listeners) {
            listeners.forEach { listener ->
                listener.onNewMessage(priority, tag, message)
            }
        }
    }
}

public interface LogListener {
    public fun onNewMessage(priority: Int, tag: String, message: String)
}
