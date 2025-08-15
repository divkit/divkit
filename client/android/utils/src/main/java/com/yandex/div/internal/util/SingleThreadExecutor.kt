package com.yandex.div.internal.util

import com.yandex.div.internal.Assert
import com.yandex.div.core.annotations.InternalApi
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Future
import java.util.concurrent.FutureTask


/**
 * A utility class that turns multi-thread executor into effectively a single-threaded executor. The runnables
 * will work in different threads, but no 2 runnables may be working concurrently.
 *
 * Runnables are executed in order they were posted.
 *
 * The exception handing strategy is as follows:
 * runtime exceptions are passed to a handler and could or could not be consumed;
 * errors has no special treatment and are likely to crash VM (since UncaughtExceptionHandler is believed to be
 * unable to consume exceptions);
 * any runtime exceptions in the local code are not handled, but there some safety section that preserves the main
 * invariants.
 */
@InternalApi
public abstract class SingleThreadExecutor(
    private val executor: Executor,
    private val threadNameSuffix: String
) {

    private val monitor = Any()
    private var currentWorker: Worker? = null
    private var passedTasks: MutableList<Runnable>? = null

    public fun post(task: Runnable) {
        var newWorker: Worker? = null
        synchronized(monitor) {
            addTaskLocked(task)
            if (currentWorker == null) {
                newWorker = Worker()
                currentWorker = newWorker
            }
        }
        if (newWorker != null) {
            executor.execute(newWorker)
        }
    }

    private fun addTaskLocked(task: Runnable) {
        if (passedTasks == null) {
            passedTasks = ArrayList(2)
        }
        passedTasks?.add(task)
    }

    public fun submit(task: Runnable): Future<*> {
        val future = FutureTask(task, null)
        post(future)
        return future
    }

    public fun <T> submit(callable: Callable<T>): Future<T> {
        val future = FutureTask(callable)
        post(future)
        return future
    }

    protected abstract fun handleError(e: RuntimeException)

    private inner class Worker : NamedRunnable(threadNameSuffix) {

        override fun execute() {
            var tasks: MutableList<Runnable>?
            synchronized(monitor) {
                if (currentWorker != this || passedTasks == null) {
                    Assert.fail("We shouldn't create excessive workers")
                    return
                }
                tasks = passedTasks
                passedTasks = null
            }

            var isGoingOn = true
            while (isGoingOn) {
                var normalExecution = false
                try {
                    tasks?.forEach { task ->
                        try {
                            task.run()
                        } catch (e: RuntimeException) {
                            handleError(e)
                        }
                    }
                    normalExecution = true
                } finally {
                    synchronized(monitor) {
                        if (normalExecution && passedTasks != null) {
                            tasks = passedTasks
                            passedTasks = null
                            // An extra work is left for us, we have to start the cycle anew.
                        } else {
                            currentWorker = null
                            isGoingOn = false
                        }
                    }
                }
            }
        }
    }
}
