package com.yandex.div.internal.viewpool

import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.yandex.div.core.util.Assert
import com.yandex.div.internal.util.removeOrThrow
import com.yandex.div.util.getOrThrow
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal class AdvanceViewPool(
        private val profiler: ViewPoolProfiler?,
        private val viewCreator: ViewCreator
) : ViewPool {

    private val channels: MutableMap<String, Channel<out View>> = androidx.collection.ArrayMap()

    @AnyThread
    override fun <T : View> register(tag: String, factory: ViewFactory<T>, capacity: Int) {
        synchronized(channels) {
            if (tag in channels) {
                Assert.fail("Factory is already registered")
                return
            }
            channels[tag] = Channel(tag, profiler, factory, viewCreator, capacity)
        }
    }

    @AnyThread
    override fun unregister(tag: String) {
        val channel = synchronized(channels) {
            if (tag !in channels) {
                Assert.fail("Factory is not registered")
                return
            }
           channels.removeOrThrow(tag)
        }

        channel.stop()
    }

    @AnyThread
    override fun <T : View> obtain(tag: String): T {
        val channel = synchronized(channels) {
            channels.getOrThrow(tag, "Factory is not registered")
        }

        @Suppress("UNCHECKED_CAST")
        return channel.extractView() as T
    }

    internal class Channel<T : View>(val viewName: String,
                                     private val profiler: ViewPoolProfiler?,
                                     private val viewFactory: ViewFactory<T>,
                                     private val viewCreator: ViewCreator,
                                     capacity: Int) {

        private val viewQueue: BlockingQueue<T> = ArrayBlockingQueue(capacity, false)
        private val stopped = AtomicBoolean(false)

        val notEmpty: Boolean = viewQueue.isNotEmpty()

        init {
            for (i in 0 until capacity) {
                viewCreator.request(this, 0)
            }
        }

        @AnyThread
        fun extractView(): T {
            var view: T? = null
            var duration = profile { view = viewQueue.poll() }

            if (view == null) {
                duration = profile { view = extractViewBlocked() }
                profiler?.onViewObtainedWithBlock(viewName, duration)
            } else {
                profiler?.onViewObtainedWithoutBlock(duration)
            }
            requestViewCreation()
            return view!!  // there is no any chance for null
        }

        @AnyThread
        private fun extractViewBlocked(): T {
            return try {
                viewCreator.promote(this)
                viewQueue.poll(MAX_WAITING_TIME, TimeUnit.MILLISECONDS) ?: viewFactory.createView()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                viewFactory.createView()
            }
        }

        private fun requestViewCreation() {
            val duration = profile {
                val priority = viewQueue.size
                viewCreator.request(this, priority)
            }
            profiler?.onViewRequested(duration)
        }

        @WorkerThread
        fun createAndEnqueueView() {
            val stopped = stopped.get()
            if (!stopped) {
                try {
                    val view = viewFactory.createView()
                    viewQueue.offer(view)
                } catch (ignored: Exception) { }
            }
        }

        fun stop() {
            stopped.set(true)
            viewQueue.clear()
        }

        private inline fun profile(crossinline section: () -> Unit): Long {
            val start = System.nanoTime()
            section()
            return System.nanoTime() - start
        }

        companion object {
            private const val MAX_WAITING_TIME = 16L
        }
    }
}
