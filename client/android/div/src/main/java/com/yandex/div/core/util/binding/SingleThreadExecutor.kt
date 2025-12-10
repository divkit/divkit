package com.yandex.div.core.util.binding

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

internal class SingleThreadExecutor private constructor(
    private val threadFactory: NamedThreadFactory
) : Executor by Executors.newSingleThreadExecutor(threadFactory) {

    val thread: Thread
        get() = threadFactory.thread

    companion object {

        @JvmStatic
        fun create(threadName: String): SingleThreadExecutor {
            val threadFactory = NamedThreadFactory(threadName)
            return SingleThreadExecutor(threadFactory)
        }
    }

    private class NamedThreadFactory(
        private val threadName: String
    ) : ThreadFactory {

        val thread = DeferredTargetThread(threadName).apply {
            isDaemon = false
            priority = Thread.NORM_PRIORITY
        }

        override fun newThread(r: Runnable): Thread {
            return thread.apply {
                target = r
            }
        }
    }

    private class DeferredTargetThread(name: String) : Thread(null, null, name, 0) {

        var target: Runnable? = null

        override fun run() {
            target?.run()
        }
    }
}
