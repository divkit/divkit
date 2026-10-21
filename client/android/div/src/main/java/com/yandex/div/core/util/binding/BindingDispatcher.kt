package com.yandex.div.core.util.binding

import androidx.annotation.MainThread
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.PreparedCollectionViewHoldersPool
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

    val hasPendingAsyncBindings: Boolean
        get() = synchronized(pendingTasksLock) {
            activeTask != null || pendingTasks.isNotEmpty()
        }

    val isCollectingMainThreadActions: Boolean
        get() = Thread.currentThread() === executor.bindingThread && deferMainThreadAction

    val currentGeneration: Int get() = bindingGeneration.get()

    val preparedViewHoldersPool = PreparedCollectionViewHoldersPool()

    private var deferMainThreadAction = false
    private val mainThreadActions = mutableListOf<Action>()
    private val pendingTasksLock = Any()
    private val pendingTasks = ArrayDeque<PendingTask>()
    private val pendingCoalescingTasks = mutableMapOf<Any, PendingTask>()
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
        preparedViewHoldersPool.invalidate(divView::logError)
        val nextTask = synchronized(pendingTasksLock) {
            pendingTasks.removeAll { it.type.cancellable }
            pendingCoalescingTasks.values.removeAll { it.type.cancellable }

            val task = activeTask
            if (task != null && tryCancelNotStartedTaskLocked(task)) {
                activeTask = null
                activeTaskStarted = false
            }
            takeNextTaskLocked()
        }
        nextTask?.let(::submitTask)
    }

    private fun tryCancelNotStartedTaskLocked(task: PendingTask): Boolean {
        if (!task.type.cancellable || activeTaskStarted) return false
        val bindingTask = when (task) {
            is MainThreadCall -> return true
            is BindingTask<*> -> task
        }
        val runnable = bindingTask.runnable ?: return false
        if (!executor.remove(runnable)) return false

        bindingTask.reservationThread?.let(criticalSection::cancelReservationFor)
        return true
    }

    /**
     * Use to schedule new task for background binding. Tasks run one by one in FIFO order.
     */
    fun <T> runOnBindingThread(
        type: BindingTaskType,
        onComplete: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        coalescingKey: Any? = null,
        block: () -> T
    ) {
        require(coalescingKey == null || (onComplete == null && onError == null)) {
            "Coalescing tasks must not have completion or error callbacks"
        }
        enqueueTask(BindingTask(type, bindingGeneration.get(), coalescingKey, onComplete, onError, block))
    }

    private fun runBindingTask(task: BindingTask<*>) {
        if (!tryStartTask(task)) {
            return
        }
        if (!task.isUpToDate()) {
            criticalSection.cancelReservationFor(Thread.currentThread())
            completeTask(task)
            return
        }

        val handle = criticalSection.enter()
        if (!task.isUpToDate()) {
            finishTask(task, handle)
            return
        }
        bind(task, handle)
    }

    private fun <T> bind(task: BindingTask<T>, handle: Disposable) {
        val preparedHolders = PreparedCollectionViewHoldersPool.BindingBatch(
            collectionId = System.identityHashCode(divView),
            generation = task.generation,
        )
        try {
            val (result, deferredActions) = collectMainThreadAction {
                preparedViewHoldersPool.collect(preparedHolders) { task.block() }
            }
            if (deferredActions.isEmpty() && task.onComplete == null) {
                finishTask(task, handle, preparedHolders)
                return
            }
            UiThreadHandler.postOnMainThread {
                criticalSection.transferToCurrentThread()
                completeOnMain(task, handle, preparedHolders, result, deferredActions)
            }
        } catch (e: Throwable) {
            finishTask(task, handle, preparedHolders)
            UiThreadHandler.postOnMainThread {
                divView.logError(e)
                if (task.isUpToDate()) {
                    task.onError?.invoke(e)
                }
            }
        }
    }

    private fun <T> completeOnMain(
        task: BindingTask<T>,
        handle: Disposable,
        preparedHolders: PreparedCollectionViewHoldersPool.BindingBatch,
        result: T,
        deferredActions: List<Action>,
    ) {
        var applied = false
        try {
            for (action in deferredActions) {
                if (!task.isUpToDate()) {
                    return
                }
                action()
            }
            if (task.isUpToDate()) {
                task.onComplete?.invoke(result)
                applied = true
            }
        } catch (e: Throwable) {
            divView.logError(e)
            if (task.isUpToDate()) {
                task.onError?.invoke(e)
            }
        } finally {
            finishTask(task, handle, if (applied) null else preparedHolders)
        }
    }

    private fun runMainThreadCall(task: MainThreadCall) {
        if (!tryStartTask(task)) {
            return
        }
        if (!task.isUpToDate()) {
            completeTask(task)
            return
        }

        val handle = criticalSection.tryEnter()
        if (handle == null) {
            retryMainThreadCallLater(task)
            return
        }
        try {
            task.action()
        } finally {
            finishTask(task, handle)
        }
    }

    private fun retryMainThreadCallLater(task: MainThreadCall) {
        synchronized(pendingTasksLock) {
            if (activeTask === task) {
                activeTaskStarted = false
            }
        }
        UiThreadHandler.get().postDelayed({ runMainThreadCall(task) }, MAIN_THREAD_CALL_RETRY_DELAY_MS)
    }

    private fun tryStartTask(task: PendingTask): Boolean {
        synchronized(pendingTasksLock) {
            if (activeTask !== task) {
                return false
            }
            activeTaskStarted = true
            return true
        }
    }

    private fun finishTask(
        task: PendingTask,
        handle: Disposable,
        discardedHolders: PreparedCollectionViewHoldersPool.BindingBatch? = null,
    ) {
        try {
            discardedHolders?.release(divView::logError)
        } finally {
            // Holder cleanup invokes host extensions; even a failure must unblock the next binding.
            criticalSection.exit(handle)
            completeTask(task)
        }
    }

    private fun PendingTask.isUpToDate(): Boolean = !type.cancellable || bindingGeneration.get() == generation

    private fun enqueueTask(pendingTask: PendingTask) {
        val nextTask = synchronized(pendingTasksLock) {
            val coalescingKey = pendingTask.coalescingKey
            if (coalescingKey != null) {
                pendingCoalescingTasks.put(coalescingKey, pendingTask)?.let(pendingTasks::remove)
            }
            pendingTasks.addLast(pendingTask)
            takeNextTaskLocked()
        }
        nextTask?.let(::submitTask)
    }

    private fun submitTask(task: PendingTask) {
        var nextTask: PendingTask? = task
        while (nextTask != null) {
            val currentTask: PendingTask = nextTask
            nextTask = when (currentTask) {
                is MainThreadCall -> {
                    UiThreadHandler.postOnMainThread { runMainThreadCall(currentTask) }
                    null
                }
                is BindingTask<*> -> {
                    val error = trySubmitTask(currentTask) ?: return
                    reportSubmissionError(currentTask, error)
                    releaseActiveTask(currentTask)
                }
            }
        }
    }

    private fun trySubmitTask(task: BindingTask<*>): Throwable? {
        val bindingThread = try {
            executor.ensureThreadCreated()
        } catch (error: Throwable) {
            return error
        }

        val runnable = Runnable { runBindingTask(task) }
        task.reservationThread = bindingThread
        task.runnable = runnable
        criticalSection.reserveFor(bindingThread)
        return try {
            executor.execute(runnable)
            null
        } catch (error: Throwable) {
            criticalSection.cancelReservationFor(bindingThread)
            task.runnable = null
            task.reservationThread = null
            error
        }
    }

    private fun reportSubmissionError(task: BindingTask<*>, error: Throwable) {
        UiThreadHandler.postOnMainThread {
            divView.logError(error)
            task.onError?.invoke(error)
        }
    }

    private fun releaseActiveTask(task: PendingTask): PendingTask? {
        return synchronized(pendingTasksLock) {
            if (activeTask !== task) {
                return null
            }
            activeTask = null
            activeTaskStarted = false
            takeNextTaskLocked()
        }
    }

    private fun completeTask(task: PendingTask) {
        releaseActiveTask(task)?.let(::submitTask)
    }

    private fun takeNextTaskLocked(): PendingTask? {
        if (activeTask != null) {
            return null
        }
        return pendingTasks.pollFirst()?.also { task ->
            val key = task.coalescingKey
            if (key != null && pendingCoalescingTasks[key] === task) {
                pendingCoalescingTasks.remove(key)
            }
            activeTask = task
        }
    }

    private inline fun <T> collectMainThreadAction(block: () -> T): Pair<T, List<Action>> {
        try {
            deferMainThreadAction = true
            val result = block()
            val snapshot = mainThreadActions.toList()
            return result to snapshot
        } finally {
            mainThreadActions.clear()
            deferMainThreadAction = false
            divView.inMiddleOfBind = false
        }
    }

    inline fun runMainThreadAction(type: BindingTaskType, crossinline action: Action) {
        if (UiThreadHandler.get().isMainThread()) {
            action()
        } else {
            postMainThreadAction(type) { action() }
        }
    }

    fun postMainThreadAction(type: BindingTaskType, action: Action) {
        if (isCollectingMainThreadActions) {
            mainThreadActions.add(action)
        } else {
            // Other owners and threads must use this owner's FIFO, without borrowing its lock handle.
            runOnBindingThread(type) {
                mainThreadActions.add(action)
            }
        }
    }

    /**
     * Use to run task on current binding thread.
     * If background binding not in progress, executes on calling thread.
     * If background binding in progress, executes on background binding thread.
     */
    fun runWithinBindingContext(
        type: BindingTaskType,
        coalescingKey: Any? = null,
        block: () -> Unit
    ) {
        if (isBackgroundBindingInProgress && UiThreadHandler.get().isMainThread()) {
            runOnBindingThread(type, coalescingKey = coalescingKey, block = block)
        } else {
            block()
        }
    }

    /**
     * Use from methods which could be externally called from main thread while background binding is in progress.
     * In such case, handles the call according to [policy] and returns [fallback].
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
    fun <T> withLock(
        fallback: T,
        policy: MainThreadTaskPolicy = MainThreadTaskPolicy.REJECT,
        onBusy: Action? = null,
        block: () -> T,
    ): T {
        if (!UiThreadHandler.get().isMainThread()) {
            return withLockInternal(block)
        }
        val handle = tryEnterOnMain(policy)
        if (handle == null) {
            handleBusy(policy, onBusy) { block() }
            return fallback
        }
        return handle.use { block() }
    }

    private fun tryEnterOnMain(policy: MainThreadTaskPolicy): Disposable? = when (policy) {
        MainThreadTaskPolicy.REJECT -> criticalSection.tryEnter()
        MainThreadTaskPolicy.DEFER -> tryEnterAfterPendingTasks()
        MainThreadTaskPolicy.REPLACE -> tryEnterAfterPendingTasks() ?: run {
            cancelPendingTasks()
            tryEnterAfterPendingTasks()
        }
    }

    private fun handleBusy(policy: MainThreadTaskPolicy, onBusy: Action?, block: Action) {
        when {
            onBusy != null -> onBusy()
            policy == MainThreadTaskPolicy.REJECT -> reportLockFail()
            policy == MainThreadTaskPolicy.DEFER ->
                enqueueTask(MainThreadCall(BindingTaskType.DATA, bindingGeneration.get(), block))
            policy == MainThreadTaskPolicy.REPLACE -> runOnBindingThread(BindingTaskType.DATA, block = block)
        }
    }

    private fun tryEnterAfterPendingTasks(): Disposable? {
        if (criticalSection.isHeldBy(Thread.currentThread())) {
            return criticalSection.tryEnter()
        }
        return synchronized(pendingTasksLock) {
            if (activeTask == null && pendingTasks.isEmpty()) criticalSection.tryEnter() else null
        }
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
        const val MAIN_THREAD_CALL_RETRY_DELAY_MS = 16L
        const val MESSAGE_LOCK_FAIL = "Trying to run UI thread binding operation while background one in progress. " +
            "Such actions may cause deadlocks, so your call is terminated. Fix this call ASAP."
        const val MESSAGE_LOCK_FAIL_WITH_ASSERTS_OFF = "Looks like asserts are turned off, so your call received default return value."
    }

    private sealed class PendingTask(
        val type: BindingTaskType,
        val generation: Int,
        val coalescingKey: Any?,
    )

    private class BindingTask<T>(
        type: BindingTaskType,
        generation: Int,
        coalescingKey: Any?,
        val onComplete: ((T) -> Unit)?,
        val onError: ((Throwable) -> Unit)?,
        val block: () -> T,
    ) : PendingTask(type, generation, coalescingKey) {
        @Volatile
        var reservationThread: Thread? = null

        @Volatile
        var runnable: Runnable? = null
    }

    private class MainThreadCall(
        type: BindingTaskType,
        generation: Int,
        val action: Action,
    ) : PendingTask(type, generation, coalescingKey = null)
}
