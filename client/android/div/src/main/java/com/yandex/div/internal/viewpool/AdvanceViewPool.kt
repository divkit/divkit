package com.yandex.div.internal.viewpool

import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.collection.ArrayMap
import com.yandex.div.internal.Assert
import com.yandex.div.internal.util.getOrThrow
import com.yandex.div.internal.util.removeOrThrow
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal class AdvanceViewPool(
    private val profiler: ViewPoolProfiler?,
    private val viewCreator: ViewCreator
) : ViewPool {

    private val viewFactories: MutableMap<String, ViewFactory<out View>> = ArrayMap()

    @AnyThread
    override fun <T : View> register(tag: String, factory: ViewFactory<T>, capacity: Int) {
        synchronized(viewFactories) {
            if (tag in viewFactories) {
                Assert.fail("Factory is already registered")
                return
            }
            viewFactories[tag] = if (capacity == 0) {
                factory.attachProfiler(tag, profiler)
            } else {
                Channel(tag, profiler, factory, viewCreator, capacity)
            }
        }
    }

    @AnyThread
    override fun unregister(tag: String) {
        val viewFactory = synchronized(viewFactories) {
            if (tag !in viewFactories) {
                Assert.fail("Factory is not registered")
                return
            }
            viewFactories.removeOrThrow(tag)
        }

        if (viewFactory is Channel) {
            viewFactory.stop()
        }
    }

    @AnyThread
    override fun <T : View> obtain(tag: String): T {
        val viewFactory = synchronized(viewFactories) {
            viewFactories.getOrThrow(tag, "Factory is not registered")
        }

        @Suppress("UNCHECKED_CAST")
        return viewFactory.createView() as T
    }

    internal class Channel<T : View>(
        val viewName: String,
        private val profiler: ViewPoolProfiler?,
        private val viewFactory: ViewFactory<T>,
        private val viewCreator: ViewCreator,
        capacity: Int
    ) : ViewFactory<T> {
        override fun createView(): T = extractView()

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
                profiler?.onViewObtainedWithBlock(viewName, duration, viewQueue.size)
            } else {
                profiler?.onViewObtainedWithoutBlock(viewName, duration, viewQueue.size)
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
            profiler?.onViewRequested(viewName, duration, viewQueue.size)
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

        companion object {
            private const val MAX_WAITING_TIME = 16L
        }
    }

    companion object {
        private inline fun profile(crossinline section: () -> Unit): Long {
            val start = System.nanoTime()
            section()
            return System.nanoTime() - start
        }

        private fun <T : View> ViewFactory<T>.attachProfiler(
            viewName: String, profiler: ViewPoolProfiler?
        ): ViewFactory<T> {
            return ViewFactory {
                var view: T? = null
                val duration = profile { view = createView() }
                profiler?.onViewObtainedWithBlock(viewName, duration, 0)
                view!!
            }
        }
    }
}
