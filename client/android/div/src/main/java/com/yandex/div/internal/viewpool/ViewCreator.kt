package com.yandex.div.internal.viewpool

import com.yandex.div.histogram.CpuUsageHistogramReporter
import com.yandex.div.internal.util.weak
import java.util.PriorityQueue

internal class ViewCreator(cpuUsageHistogramReporter: CpuUsageHistogramReporter) {

    private val creatorThread = ViewCreatorThread("ViewPoolThread", cpuUsageHistogramReporter)

    init {
        creatorThread.start()
    }

    internal fun request(channel: AdvanceViewPool.Channel<*>, priority: Int) {
        creatorThread.taskQueue.offer(CreateViewTask(channel, priority))
    }

    internal fun promote(channel: AdvanceViewPool.Channel<*>) {
        if (channel.viewName == creatorThread.currentViewName || channel.notEmpty) return

        creatorThread.taskQueue.batch {
            // double check
            if (channel.viewName == creatorThread.currentViewName || channel.notEmpty) return

            creatorThread.taskQueue.removeFirstIf { task -> task.viewName == channel.viewName }
            creatorThread.taskQueue.offer(CreateViewTask(channel, TASK_HIGHEST_PRIORITY))
        }
    }

    private class ViewCreatorThread(
        name: String,
        private val cpuUsageHistogramReporter: CpuUsageHistogramReporter
    ) : Thread(name) {

        val taskQueue = BatchBlockingQueue(
            PriorityQueue<CreateViewTask>(TASK_QUEUE_INITIAL_CAPACITY)
        )

        @Volatile
        var currentViewName: String? = null
            private set

        init {
            priority = THREAD_DEFAULT_PRIORITY
        }

        override fun run() {
            val histogramReporting =
                cpuUsageHistogramReporter.startReporting(CPU_USAGE_HISTOGRAM_NAME,
                    android.os.Process.myTid())
            try {
                while (true) {
                    createView()
                }
            } catch (e: InterruptedException) {
                currentThread().interrupt()
            } finally {
                histogramReporting.cancel()
            }
        }

        @Throws(InterruptedException::class)
        private fun createView() {
            val task: CreateViewTask = taskQueue.poll() ?: run {
                // We lower the priority of the creator thread in order
                // to reduce delays at inserts to the task queue.
                try {
                    priority = THREAD_LOW_PRIORITY
                    taskQueue.take()
                } finally {
                    priority = THREAD_DEFAULT_PRIORITY
                }
            }

            currentViewName = task.viewName
            task.run()
            currentViewName = null
        }
    }

    private class CreateViewTask(channel: AdvanceViewPool.Channel<*>,
                                 private val priority: Int) : Runnable, Comparable<CreateViewTask> {

        val viewName = channel.viewName
        private val channelRef by weak(channel)

        override fun run() {
            channelRef?.createAndEnqueueView()
        }

        override fun compareTo(other: CreateViewTask): Int {
            val priorityDiff = priority - other.priority
            if (priorityDiff != 0) {
                return priorityDiff
            }
            return if (viewName == other.viewName) 0 else 1
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CreateViewTask

            if (viewName != other.viewName) return false
            if (priority != other.priority) return false

            return true
        }

        override fun hashCode(): Int {
            var result = 223
            result = 31 * result + priority
            result = 31 * result + viewName.hashCode()
            return result
        }
    }

    private companion object {
        private const val THREAD_DEFAULT_PRIORITY = Thread.NORM_PRIORITY
        private const val THREAD_LOW_PRIORITY = 3
        private const val TASK_HIGHEST_PRIORITY = -1
        private const val TASK_QUEUE_INITIAL_CAPACITY = 32
        private const val CPU_USAGE_HISTOGRAM_NAME = "Div.ViewPool.CPU"
    }
}
