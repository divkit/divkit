package com.yandex.div.internal.util

import android.os.Handler
import android.os.Looper

/**
 * Provides static access to the main thread handler.
 */
object UiThreadHandler {

    private val INSTANCE = Handler(Looper.getMainLooper())

    /**
     * Returns value indicating if current thread is main thread.
     */
    @JvmStatic
    fun isMainThread() = Thread.currentThread() == Looper.getMainLooper().thread

    /**
     * Returns main thread handler.
     */
    @JvmStatic
    fun get(): Handler {
        return INSTANCE
    }

    /**
     * Posts given function on the main thread.
     */
    fun postOnMainThread(runnable: () -> Unit) = INSTANCE.post(runnable)

    /**
     * Executes a given [Runnable] on main thread.
     * Executes runnable immediately if current thread is main thread, posts runnable to main thread handler otherwise.
     */
    @JvmStatic
    fun executeOnMainThread(runnable: Runnable) {
        if (isMainThread()) {
            runnable.run()
        } else {
            INSTANCE.post(runnable)
        }
    }

    /**
     * Executes a given [action] on main thread.
     * Executes runnable immediately if current thread is main thread, posts runnable to main thread handler otherwise.
     */
    inline fun executeOnMainThread(crossinline action: () -> Unit) {
        if (isMainThread()) {
            action()
        } else {
            get().post { action() }
        }
    }
}
