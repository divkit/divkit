package com.yandex.div.internal.util

import android.os.Handler
import android.os.Looper
import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Provides static access to the main thread handler.
 */
@InternalApi
public object UiThreadHandler {

    private val INSTANCE = Handler(Looper.getMainLooper())

    /**
     * Returns main thread instance.
     */
    @JvmStatic
    public fun mainThread(): Thread = Looper.getMainLooper().thread

    /**
     * Returns value indicating if current thread is main thread.
     */
    @JvmStatic
    public fun isMainThread(): Boolean = Thread.currentThread() == mainThread()

    /**
     * Returns main thread handler.
     */
    @JvmStatic
    public fun get(): Handler {
        return INSTANCE
    }

    /**
     * Posts given function on the main thread.
     */
    public fun postOnMainThread(runnable: () -> Unit): Boolean = INSTANCE.post(runnable)

    /**
     * Executes a given [Runnable] on main thread.
     * Executes runnable immediately if current thread is main thread, posts runnable to main thread handler otherwise.
     */
    @JvmStatic
    public fun executeOnMainThread(runnable: Runnable) {
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
    public inline fun executeOnMainThread(crossinline action: () -> Unit) {
        if (isMainThread()) {
            action()
        } else {
            get().post { action() }
        }
    }

    /**
     * Executes a given [action] on main thread.
     * Executes runnable immediately if current thread is main thread, locks current thread and
     * waits execution of the runnable on the main thread handler otherwise.
     *
     * @return result of the runnable execution.
     */
    public inline fun <T> executeOnMainThreadBlocking(crossinline action: () -> T): T {
        return if (isMainThread()) {
            action()
        } else {
            runBlocking(Dispatchers.Main) {
                action()
            }
        }
    }
}
