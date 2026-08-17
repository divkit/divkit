package com.yandex.div.core.util.binding

import androidx.annotation.MainThread
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.util.UiThreadHandler
import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

private typealias Action = () -> Unit

@DivViewScope
internal class BindingDispatcher @Inject constructor(
    private val divView: Div2View,
    private val criticalSection: BindingCriticalSection,
    private val executor: BindingThreadExecutor,
) {

    val isBackgroundBindingInProgress: Boolean
        get() {
            val bindingThread = executor.bindingThread ?: return false
            return criticalSection.isHeldBy(bindingThread) || criticalSection.isReservedFor(bindingThread)
        }

    private var deferMainThreadAction = false
    private val mainThreadActions = mutableListOf<Action>()
    private val pendingTasksLock = Any()
    private val pendingTasks = ArrayDeque<PendingTask>()
    private val bindingGeneration = AtomicInteger()
    private var activeTask: PendingTask? = null
    private var activeTaskStarted = false

    /**
     * Cancels queued work and invalidates running work before its main-thread continuation.
     * Must be called on main to serialize invalidation with continuation application.
     */
    @MainThread
    fun cancelPendingTasks() {
        bindingGeneration.incrementAndGet()
        synchronized(pendingTasksLock) {
            pendingTasks.clear()

            val task = activeTask
            if (task != null && !activeTaskStarted && executor.remove(task.runnable)) {
                activeTask = null
                activeTaskStarted = false
                task.reservationThread?.let(criticalSection::cancelReservationFor)
            }
        }
    }

    /**
     * Use to schedule new task for background binding.
     */
    inline fun <T> runOnBindingThread(
        noinline onComplete: ((T) -> Unit)? = null,
        noinline onError: ((Throwable) -> Unit)? = null,
        crossinline block: () -> T
    ) {
        val generation = bindingGeneration.get()
        lateinit var task: Runnable
        task = Runnable {
            synchronized(pendingTasksLock) {
                if (!isActiveTaskLocked(task)) {
                    return@Runnable
                }
                activeTaskStarted = true
            }
            if (bindingGeneration.get() != generation) {
                criticalSection.cancelReservationFor(Thread.currentThread())
                completeTask(task)
                return@Runnable
            }

            val handle = criticalSection.enter()
            if (bindingGeneration.get() != generation) {
                criticalSection.exit(handle)
                completeTask(task)
                return@Runnable
            }
            try {
                val (result, deferredActions) = collectMainThreadAction {
                    block()
                }
                if (deferredActions.isEmpty() && onComplete == null) {
                    criticalSection.exit(handle)
                    completeTask(task)
                } else {
                    UiThreadHandler.postOnMainThread {
                        criticalSection.transferToCurrentThread()
                        val isCurrent = bindingGeneration.get() == generation
                        try {
                            if (isCurrent) {
                                deferredActions.forEach { action ->
                                    action.invoke()
                                }
                                onComplete?.invoke(result)
                            }
                        } catch (e: Throwable) {
                            divView.logError(e)
                            if (isCurrent) {
                                onError?.invoke(e)
                            }
                        } finally {
                            criticalSection.exit(handle)
                            completeTask(task)
                        }
                    }
                }
            } catch (e: Throwable) {
                criticalSection.exit(handle)
                completeTask(task)
                UiThreadHandler.postOnMainThread {
                    divView.logError(e)
                    if (bindingGeneration.get() == generation) {
                        onError?.invoke(e)
                    }
                }
            }
        }
        enqueueTask(task, onError)
    }

    private fun enqueueTask(task: Runnable, onError: ((Throwable) -> Unit)?) {
        val nextTask = synchronized(pendingTasksLock) {
            pendingTasks.addLast(PendingTask(task, onError))
            takeNextTaskLocked()
        }
        nextTask?.let(::submitTask)
    }

    private fun submitTask(task: PendingTask) {
        var nextTask: PendingTask? = task
        while (nextTask != null) {
            val currentTask = nextTask
            val error = trySubmitTask(currentTask)
            if (error == null) {
                return
            }

            reportSubmissionError(currentTask, error)
            nextTask = completeRejectedTask(currentTask)
        }
    }

    private fun trySubmitTask(task: PendingTask): Throwable? {
        val bindingThread = try {
            executor.ensureThreadCreated()
        } catch (error: Throwable) {
            return error
        }

        task.reservationThread = bindingThread
        criticalSection.reserveFor(bindingThread)
        return try {
            executor.execute(task.runnable)
            null
        } catch (error: Throwable) {
            criticalSection.cancelReservationFor(bindingThread)
            task.reservationThread = null
            error
        }
    }

    private fun reportSubmissionError(task: PendingTask, error: Throwable) {
        UiThreadHandler.postOnMainThread {
            divView.logError(error)
            task.onError?.invoke(error)
        }
    }

    private fun completeRejectedTask(task: PendingTask): PendingTask? {
        return synchronized(pendingTasksLock) {
            if (activeTask !== task) {
                return null
            }
            activeTask = null
            activeTaskStarted = false
            takeNextTaskLocked()
        }
    }

    private fun completeTask(task: Runnable) {
        val nextTask = synchronized(pendingTasksLock) {
            if (activeTask?.runnable !== task) {
                return
            }
            activeTask = null
            activeTaskStarted = false
            takeNextTaskLocked()
        }
        nextTask?.let(::submitTask)
    }

    private fun isActiveTaskLocked(task: Runnable): Boolean {
        return activeTask?.runnable === task
    }

    private fun takeNextTaskLocked(): PendingTask? {
        if (activeTask != null) {
            return null
        }
        return pendingTasks.pollFirst()?.also { activeTask = it }
    }

    private inline fun <T> collectMainThreadAction(block: () -> T): Pair<T, List<Action>> {
        try {
            deferMainThreadAction = true
            val result = block()
            val snapshot = mainThreadActions.toList()
            mainThreadActions.clear()
            return result to snapshot
        } finally {
            deferMainThreadAction = false
        }
    }

    inline fun runMainThreadAction(crossinline action: Action) {
        if (UiThreadHandler.get().isMainThread()) {
            action()
        } else {
            postMainThreadAction { action() }
        }
    }

    fun postMainThreadAction(action: Action) {
        if (deferMainThreadAction) {
            mainThreadActions.add(action)
        } else {
            postToMainThread(action)
        }
    }

    private fun postToMainThread(action: Action) {
        val handle = criticalSection.enter()
        var posted = false

        try {
            UiThreadHandler.postOnMainThread {
                criticalSection.transferToCurrentThread()
                try {
                    action()
                } finally {
                    criticalSection.exit(handle)
                }
            }
            posted = true
        } finally {
            if (!posted) {
                criticalSection.exit(handle)
            }
        }
    }

    /**
     * Use to run task on current binding thread.
     * If background binding not in progress, executes on calling thread.
     * If background binding in progress, executes on background binding thread.
     */
    inline fun <T> runWithinBindingContext(
        crossinline block: () -> T
    ) {
        if (isBackgroundBindingInProgress && UiThreadHandler.get().isMainThread()) {
            runOnBindingThread(null, null, block)
        } else {
            block()
        }
    }

    /**
     * Use from methods which could be externally called from main thread while background binding is in progress.
     * In such case, rejects the execution.
     *
     * This method is a shortcut for `safeBindingLock(Unit, block)`.
     */
    inline fun withLock(block: () -> Unit): Unit = withLock(Unit, block)

    /**
     * Use from methods which could be externally called from main thread while background binding is in progress.
     * In such case, rejects the execution returning [fallback].
     *
     * Acquisition is done with [BindingCriticalSection.tryEnter] on the main thread —
     * a single atomic operation inside the section's lock. The previous implementation
     * read [isBackgroundBindingInProgress] and then called the blocking
     * [BindingCriticalSection.enter]; between those two steps the binding thread could
     * call [BindingCriticalSection.reserveFor], leaving the main thread parked indefinitely.
     * `tryEnter` collapses check and acquire into one synchronized block, so no such
     * window can open.
     *
     * Off the main thread we still use the blocking [withLockInternal] path, because
     * background binding tasks are expected to serialize against each other.
     */
    inline fun <T> withLock(fallback: T, block: () -> T): T {
        if (UiThreadHandler.get().isMainThread()) {
            val handle = criticalSection.tryEnter()
            if (handle == null) {
                reportLockFail()
                return fallback
            }
            return handle.use { block() }
        }

        return withLockInternal(block)
    }

    private inline fun <T> withLockInternal(block: () -> T): T {
        val handle = criticalSection.enter()
        handle.use {
            return block()
        }
    }

    private fun reportLockFail() {
        KAssert.fail { MESSAGE_LOCK_FAIL }
        divView.logError(RuntimeException("$MESSAGE_LOCK_FAIL $MESSAGE_LOCK_FAIL_WITH_ASSERTS_OFF"))
    }

    private companion object {
        const val MESSAGE_LOCK_FAIL = "Trying to run UI thread binding operation while background one in progress. " +
            "Such actions may cause deadlocks, so your call is terminated. Fix this call ASAP."
        const val MESSAGE_LOCK_FAIL_WITH_ASSERTS_OFF = "Looks like asserts are turned off, so your call received default return value."
    }

    private class PendingTask(
        val runnable: Runnable,
        val onError: ((Throwable) -> Unit)?,
    ) {
        @Volatile
        var reservationThread: Thread? = null
    }
}
