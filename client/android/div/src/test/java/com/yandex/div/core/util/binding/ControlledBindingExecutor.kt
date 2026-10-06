package com.yandex.div.core.util.binding

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.android.util.concurrent.PausedExecutorService
import org.robolectric.shadows.ShadowLooper

internal class ControlledBindingExecutor : AutoCloseable {
    private val worker = PausedExecutorService()
    private val workerThread = worker.submit<Thread> { Thread.currentThread() }.apply {
        check(worker.runNext())
    }.get()

    val executor = mock<BindingThreadExecutor> {
        on { bindingThread } doReturn workerThread
        on { ensureThreadCreated() } doReturn workerThread
        on { execute(any()) } doAnswer {
            worker.execute(it.getArgument<Runnable>(0))
            null
        }
    }

    fun runNext() {
        check(worker.runNext()) { "Expected a queued binding task" }
    }

    fun runWorker() {
        var completed = 0
        while (worker.hasQueuedTasks()) {
            check(++completed <= 100) { "Binding kept scheduling new tasks" }
            runNext()
        }
    }

    fun settle() {
        repeat(10) {
            runWorker()
            ShadowLooper.idleMainLooper()
            if (!worker.hasQueuedTasks()) return
        }
        error("Binding did not finish")
    }

    override fun close() {
        try {
            settle()
        } finally {
            worker.shutdownNow()
        }
    }
}
