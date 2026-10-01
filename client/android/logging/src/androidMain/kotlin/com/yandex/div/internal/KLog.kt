package com.yandex.div.internal

import com.yandex.div.logging.Severity
import com.yandex.div.logging.toCoreSeverity
import com.yandex.div.logging.toLogPriority
import com.yandex.div.core.log.Log as CoreLog

/**
 * Kotlin wrapper for [Log] using inline [Log.isEnabled] checks.
 */
public object KLog {

    private val listeners = mutableListOf<LogListener>()

    public inline fun d(tag: String, message: () -> String) {
        if (isAtLeast(Severity.DEBUG)) {
            print(Severity.DEBUG, tag, message())
        }
    }

    public inline fun d(tag: String, th: Throwable, message: () -> String) {
        if (isAtLeast(Severity.DEBUG)) {
            printSilently(Severity.DEBUG, tag, message(), th)
        }
    }

    public inline fun w(tag: String, message: () -> String) {
        if (isAtLeast(Severity.WARNING)) {
            print(Severity.WARNING, tag, message())
        }
    }

    public inline fun w(tag: String, th: Throwable, message: () -> String) {
        if (isAtLeast(Severity.WARNING)) {
            printSilently(Severity.WARNING, tag, message(), th)
        }
    }

    public inline fun i(tag: String, message: () -> String) {
        if (isAtLeast(Severity.INFO)) {
            print(Severity.INFO, tag, message())
        }
    }

    public inline fun i(tag: String, th: Throwable, message: () -> String) {
        if (isAtLeast(Severity.INFO)) {
            printSilently(Severity.INFO, tag, message(), th)
        }
    }

    public inline fun e(tag: String, message: () -> String) {
        if (isAtLeast(Severity.ERROR)) {
            print(Severity.ERROR, tag, message())
        }
    }

    public inline fun e(tag: String, th: Throwable?, message: () -> String = { "" }) {
        if (isAtLeast(Severity.ERROR)) {
            if (th == null) {
                CoreLog.e(tag, message())
            } else {
                CoreLog.e(tag, message(), th)
            }
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
    internal fun print(severity: Severity, tag: String, message: String) {
        CoreLog.print(severity.toCoreSeverity(), tag, message)

        synchronized(listeners) {
            listeners.forEach { listener ->
                listener.onNewMessage(severity.toLogPriority(), tag, message)
            }
        }
    }

    @PublishedApi
    @Deprecated("Binary compatibility with code inlined against older versions", level = DeprecationLevel.HIDDEN)
    internal fun print(priority: Int, tag: String, message: String) {
        CoreLog.print(priority.toCoreSeverity(), tag, message)

        synchronized(listeners) {
            listeners.forEach { listener ->
                listener.onNewMessage(priority, tag, message)
            }
        }
    }

    @PublishedApi
    internal fun printSilently(severity: Severity, tag: String, message: String, throwable: Throwable?) {
        if (throwable == null) {
            CoreLog.print(severity.toCoreSeverity(), tag, message)
        } else {
            CoreLog.print(severity.toCoreSeverity(), tag, message, throwable)
        }
    }
}

public interface LogListener {
    public fun onNewMessage(priority: Int, tag: String, message: String)
}
