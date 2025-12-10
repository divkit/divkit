package com.yandex.div.core.util.binding

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.util.UiThreadHandler
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
internal class BindingDispatcher @Inject constructor(
    private val divView: Div2View,
    private val criticalSection: BindingCriticalSection,
    @param:Named(Names.BACKGROUND_BINDING_EXECUTOR) private val executor: SingleThreadExecutor,
) {

    val isBackgroundBindingInProgress: Boolean
        get() = criticalSection.isHeldBy(executor.thread)

    /**
     * Use to schedule new task for background binding.
     */
    inline fun <T> runOnBindingThread(
        noinline onComplete: ((T) -> Unit)? = null,
        crossinline block: () -> T
    ) {
        criticalSection.reserveFor(executor.thread)
        executor.execute {
            val handle = criticalSection.enter()
            try {
                val result = block()
                if (onComplete != null) {
                    UiThreadHandler.postOnMainThread {
                        try {
                            onComplete(result)
                        } finally {
                            criticalSection.exit(handle)
                        }
                    }
                }
            } finally {
                if (onComplete == null) {
                    criticalSection.exit(handle)
                }
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
        if (isBackgroundBindingInProgress && UiThreadHandler.isMainThread()) {
            runOnBindingThread(null, block)
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
     */
    inline fun <T> withLock(fallback: T, block: () -> T): T {
        if (isBackgroundBindingInProgress && UiThreadHandler.isMainThread()) {
            reportLockFail()
            return fallback
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
        const val TAG = "BindingDispatcher"

        const val MESSAGE_LOCK_FAIL = "Trying to run UI thread binding operation while background one in progress. " +
            "Such actions may cause deadlocks, so your call is terminated. Fix this call ASAP."
        const val MESSAGE_LOCK_FAIL_WITH_ASSERTS_OFF = "Looks like asserts are turned off, so your call received default return value."
    }
}
