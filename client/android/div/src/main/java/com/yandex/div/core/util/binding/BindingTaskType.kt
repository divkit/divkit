package com.yandex.div.core.util.binding

/**
 * Whether [BindingDispatcher.cancelPendingTasks] drops a queued task and the main thread work of a running one.
 */
internal enum class BindingTaskType(val cancellable: Boolean) {

    /**
     * Work for the data that is being bound: binding, deferred main thread calls, visibility tracking.
     * Dropped together with its deferred main thread actions and callbacks when the data is replaced.
     */
    DATA(cancellable = true),

    /**
     * View lifecycle and cleanup work that must not be lost: detach, media release, observer registration.
     * Always runs with all its deferred main thread actions, in FIFO order with the other tasks.
     */
    LIFECYCLE(cancellable = false),
}
