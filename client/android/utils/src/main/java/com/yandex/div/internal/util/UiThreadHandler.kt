package com.yandex.div.internal.util

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Provides static access to the main thread handler.
 */
@InternalApi
public interface UiThreadHandler {

    /**
     * Returns main (UI) thread instance.
     */
    public val mainThread: Thread

    /**
     * Checks if the current thread is the main (UI) thread.
     */
    public fun isMainThread(): Boolean

    /**
     * Posts a [Runnable] to be executed on the UI thread.
     *
     * @param runnable The runnable to be executed.
     * @return true if the runnable was successfully posted, false otherwise.
     */
    public fun post(runnable: Runnable): Boolean

    /**
     * Posts a [Runnable] to be executed on the UI thread after a specified delay.
     *
     * @param runnable The runnable to be executed.
     * @param delayMs The delay in milliseconds before the runnable is executed.
     * @return true if the runnable was successfully posted, false otherwise.
     */
    public fun postDelayed(runnable: Runnable, delayMs: Long): Boolean

    public companion object {

        private var isInTestMode = false

        private val impl: UiThreadHandler
            get() = synchronized(this) {
                if (isInTestMode) {
                    TestUiThreadHandler
                } else {
                    UiThreadHandlerImpl
                }
            }

        @VisibleForTesting
        @JvmStatic
        public fun setInTestMode(enabled: Boolean): Unit = synchronized(this) {
            isInTestMode = enabled
        }

        /**
         * Returns UI thread handler instance.
         */
        @JvmStatic
        public fun get(): UiThreadHandler = impl

        /**
         * Posts given [Runnable] on the main thread.
         */
        @JvmStatic
        public fun postOnMainThread(runnable: Runnable): Boolean {
            return get().post(runnable)
        }

        /**
         * Posts given [action] on the main thread.
         */
        @JvmStatic
        public fun postOnMainThread(action: () -> Unit): Boolean {
            return get().post(action)
        }

        /**
         * Executes a given [Runnable] on main thread.
         * Executes runnable immediately if current thread is main thread, posts runnable to main thread handler otherwise.
         */
        @JvmStatic
        public fun executeOnMainThread(runnable: Runnable) {
            val handler = get()
            if (handler.isMainThread()) {
                runnable.run()
            } else {
                handler.post(runnable)
            }
        }

        /**
         * Executes a given [action] on main thread.
         * Executes runnable immediately if current thread is main thread, posts runnable to main thread handler otherwise.
         */
        @JvmStatic
        public inline fun executeOnMainThread(crossinline action: () -> Unit) {
            val handler = get()
            if (handler.isMainThread()) {
                action()
            } else {
                handler.post { action() }
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
            return if (get().isMainThread()) {
                action()
            } else {
                runBlocking(Dispatchers.Main) {
                    action()
                }
            }
        }
    }
}

private object UiThreadHandlerImpl : UiThreadHandler {

    private val handler = Handler(Looper.getMainLooper())

    override val mainThread: Thread
        get() = Looper.getMainLooper().thread

    override fun isMainThread(): Boolean {
        return Thread.currentThread() == mainThread
    }

    override fun post(runnable: Runnable): Boolean {
        return handler.post(runnable)
    }

    override fun postDelayed(runnable: Runnable, delayMs: Long): Boolean {
        return handler.postDelayed(runnable, delayMs)
    }
}

private object TestUiThreadHandler : UiThreadHandler {

    override val mainThread: Thread
        get() = Thread.currentThread()

    override fun isMainThread(): Boolean = true

    override fun post(runnable: Runnable): Boolean {
        runnable.run()
        return true
    }

    override fun postDelayed(runnable: Runnable, delayMs: Long): Boolean {
        return post(runnable)
    }
}
