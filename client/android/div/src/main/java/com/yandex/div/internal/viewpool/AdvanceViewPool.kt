package com.yandex.div.internal.viewpool

import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.collection.ArrayMap
import com.yandex.div.internal.Assert
import com.yandex.div.internal.util.getOrThrow
import com.yandex.div.internal.util.removeOrThrow
import com.yandex.div.internal.viewpool.optimization.PerformanceDependentSessionProfiler
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

internal class AdvanceViewPool(
    private val profiler: ViewPoolProfiler?,
    private val sessionProfiler: PerformanceDependentSessionProfiler,
    private val viewCreator: ViewCreator
) : ViewPool {

    private val viewFactories: MutableMap<String, Channel<out View>> = ArrayMap()

    @AnyThread
    override fun <T : View> register(tag: String, factory: ViewFactory<T>, capacity: Int) {
        synchronized(viewFactories) {
            if (tag in viewFactories) {
                Assert.fail("Factory is already registered")
                return
            }
            viewFactories[tag] = Channel(tag, profiler, sessionProfiler, factory, viewCreator, capacity)
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

        viewFactory.stop()
    }

    @AnyThread
    override fun <T : View> obtain(tag: String): T {
        val viewFactory = synchronized(viewFactories) {
            viewFactories.getOrThrow(tag, "Factory is not registered")
        }

        @Suppress("UNCHECKED_CAST")
        return viewFactory.createView() as T
    }

    @AnyThread
    override fun changeCapacity(tag: String, newCapacity: Int) {
        synchronized(viewFactories) {
            viewFactories.getOrThrow(tag, "Factory is not registered").apply {
                capacity = newCapacity
            }
        }
    }

    internal class Channel<T : View>(
        val viewName: String,
        private val profiler: ViewPoolProfiler?,
        private val sessionProfiler: PerformanceDependentSessionProfiler,
        private val viewFactory: ViewFactory<T>,
        private val viewCreator: ViewCreator,
        initCapacity: Int
    ) : ViewFactory<T> {
        override fun createView(): T = extractView()

        private val viewQueue: BlockingQueue<T> = LinkedBlockingQueue()
        private var realQueueSize = AtomicInteger(initCapacity)
        private val stopped = AtomicBoolean(false)

        val notEmpty: Boolean = viewQueue.isNotEmpty()

        @Volatile
        var capacity = initCapacity

        init {
            for (i in 0 until initCapacity) {
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
                sessionProfiler.onViewObtained(viewName, duration, viewQueue.size, true)
            } else {
                realQueueSize.decrementAndGet()
                profiler?.onViewObtainedWithoutBlock(duration)
                sessionProfiler.onViewObtained(viewName, duration, viewQueue.size, false)
            }
            requestViewCreation()
            return view!!  // there is no any chance for null
        }

        @AnyThread
        private fun extractViewBlocked(): T {
            return try {
                viewCreator.promote(this)
                viewQueue.poll(MAX_WAITING_TIME, TimeUnit.MILLISECONDS)?.also {
                    realQueueSize.decrementAndGet()
                } ?: viewFactory.createView()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                viewFactory.createView()
            }
        }

        private fun requestViewCreation() {
            if (capacity <= realQueueSize.get()) return
            val duration = profile {
                val priority = viewQueue.size
                viewCreator.request(this, priority)
                realQueueSize.incrementAndGet()
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
    }
}
