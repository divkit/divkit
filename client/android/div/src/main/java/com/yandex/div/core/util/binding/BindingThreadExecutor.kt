package com.yandex.div.core.util.binding

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal class BindingThreadExecutor private constructor(
    private val threadFactory: NamedThreadFactory,
    private val executor: FinalizableExecutorService = singleThreadExecutor(threadFactory, keepAliveTime = 1000L),
) : Executor by executor {

    val bindingThread: Thread?
        get() {
            val thread = threadFactory.thread
            return if (thread != null && thread.isAlive) {
                thread
            } else {
                null
            }
        }

    /**
     * Ensures that the binding thread is created and returns it.
     *
     * @return The binding [Thread] if successfully created.
     * @throws IllegalStateException If the binding thread could not be created.
     */
    fun ensureThreadCreated(): Thread {
        val thread = this.bindingThread
        return if (thread == null) {
            execute { Unit }
            this.bindingThread ?: throw IllegalStateException("Binding thread is not created")
        } else {
            thread
        }
    }

    val queueSize: Int
        get() = executor.queueSize

    fun remove(task: Runnable): Boolean = executor.remove(task)

    companion object {

        @JvmStatic
        fun create(threadName: String): BindingThreadExecutor {
            val threadFactory = NamedThreadFactory(threadName)
            return BindingThreadExecutor(threadFactory)
        }
    }

    private class NamedThreadFactory(
        private val threadName: String
    ) : ThreadFactory {

        @Volatile
        var thread: Thread? = null

        override fun newThread(r: Runnable): Thread {
            return Thread(r, threadName).also {
                thread = it
            }
        }
    }
}

private fun singleThreadExecutor(
    threadFactory: ThreadFactory,
    keepAliveTime: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS
): FinalizableExecutorService {
    return FinalizableExecutorService(
        executor = ThreadPoolExecutor(
            /* corePoolSize = */ 1,
            /* maximumPoolSize = */ 1,
            /* keepAliveTime = */ keepAliveTime,
            /* unit = */ timeUnit,
            /* workQueue = */ LinkedBlockingQueue(),
            /* threadFactory = */ threadFactory
        )
    )
}

private class FinalizableExecutorService(
    private val executor: ThreadPoolExecutor
) : ExecutorService by executor {

    val queueSize: Int
        get() = executor.queue.size

    fun remove(task: Runnable): Boolean = executor.remove(task)

    protected fun finalize() {
        executor.shutdown()
    }
}
