package com.yandex.div.core.util.binding

/**
 * What [BindingDispatcher.withLock] does with a main thread call that arrives while the binding
 * critical section is busy (background binding is running or tasks are queued).
 */
internal enum class MainThreadTaskPolicy {

    /**
     * Reports a lock failure and returns the fallback.
     *
     * Used by `Div2View.applyPatch` and by internal reentrant calls under the lock:
     * a patch is built for the data that is bound now, so it must not run on data bound later.
     */
    REJECT,

    /**
     * Runs the call on the main thread after all queued tasks (FIFO) and returns the fallback.
     * The call is dropped if the queue is cancelled.
     *
     * Used by `switchToState`, `resetToInitialState`, `handleAction`, `handleActionWithResult`, `handleUri`,
     * `setVariable`, `applyTimerCommand`, `applyVideoCommand`, tooltip and overflow menu calls:
     * these calls address the data that will be bound by then.
     */
    DEFER,

    /**
     * Cancels queued [BindingTaskType.DATA] tasks, then runs the call or queues it on the binding thread.
     *
     * Used by `setData` and `setDataWithStates`, where new data replaces the pending binding,
     * and by `prepareForRecycleOrCleanup` with `onBusy = cleanup()`. `setData` with the data that is already
     * bound uses [DEFER] instead, so it does not drop calls deferred during the binding.
     */
    REPLACE,
}
