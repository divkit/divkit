package com.yandex.div.internal

/**
 * Kotlin wrapper for [Log] using inline [Log.isEnabled] checks.
 */
object KLog {

    val listeners = mutableListOf<LogListener>()

    inline fun v(tag: String, message: () -> String) {
        if (Log.isEnabled()) {
            print(android.util.Log.VERBOSE, tag, message())
        }
    }

    inline fun v(tag: String, th: Throwable, message: () -> String) {
        if (Log.isEnabled()) {
            android.util.Log.v(tag, message(), th)
        }
    }

    inline fun d(tag: String, message: () -> String) {
        if (Log.isEnabled()) {
            print(android.util.Log.DEBUG, tag, message())
        }
    }

    inline fun d(tag: String, th: Throwable, message: () -> String) {
        if (Log.isEnabled()) {
            android.util.Log.d(tag, message(), th)
        }
    }

    inline fun w(tag: String, message: () -> String) {
        if (Log.isEnabled()) {
            print(android.util.Log.WARN, tag, message())
        }
    }

    inline fun w(tag: String, th: Throwable, message: () -> String) {
        if (Log.isEnabled()) {
            android.util.Log.w(tag, message(), th)
        }
    }

    inline fun i(tag: String, message: () -> String) {
        if (Log.isEnabled()) {
            print(android.util.Log.INFO, tag, message())
        }
    }

    inline fun i(tag: String, th: Throwable, message: () -> String) {
        if (Log.isEnabled()) {
            android.util.Log.i(tag, message(), th)
        }
    }

    inline fun e(tag: String, message: () -> String) {
        if (Log.isEnabled()) {
            print(android.util.Log.ERROR, tag, message())
        }
    }

    inline fun e(tag: String, th: Throwable?, message: () -> String = { "" }) {
        if (Log.isEnabled()) {
            android.util.Log.e(tag, message(), th)
        }
    }

    fun addListener(listener: LogListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: LogListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

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

interface LogListener {
    fun onNewMessage(priority: Int, tag: String, message: String)
}
