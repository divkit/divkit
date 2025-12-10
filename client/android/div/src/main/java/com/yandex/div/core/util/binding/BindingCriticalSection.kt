package com.yandex.div.core.util.binding

import androidx.annotation.AnyThread
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.internal.util.UiThreadHandler
import java.util.concurrent.locks.LockSupport
import javax.inject.Inject

/**
 * Critical section for synchronizing binding process in [com.yandex.div.core.view2.Div2View]
 *
 * Basic usage scenarios:
 * ```kotlin
 * val handle = criticalSection.enter()
 * try {
 *     // perform some actions
 * } finally {
 *     criticalSection.exit(handle)
 * }
 * ```
 *
 * The key difference from [java.util.concurrent.locks.ReentrantLock]
 * is that this section can be released by a different thread:
 * ```kotlin
 * val handle = criticalSection.enter()
 * try {
 *     // perform some actions
 * } catch (e: Exception) {
 *     criticalSection.exit(handle)
 *     throw e
 * }
 * Handler(Looper.getMainLooper()).post {
 *     try {
 *         // perform another actions
 *     } finally {
 *         criticalSection.exit(handle)
 *     }
 * }
 * ```
 */
@DivViewScope
internal class BindingCriticalSection @Inject constructor() {

    private var holder: Thread? = null
    private var entranceCounter = 0
    private val waiters = mutableListOf<Thread>()

    private val lock = Any()

    /**
     * Indicates whether the critical section is currently held by any thread.
     */
    val isHeld: Boolean
        @AnyThread get() {
            return synchronized(lock) {
                holder != null
            }
        }

    /**
     * Checks if the critical section is held by the specified thread.
     *
     * @param thread The thread to check
     * @return `true` if the critical section is held by the specified thread, `false` otherwise
     */
    @AnyThread
    fun isHeldBy(thread: Thread): Boolean {
        return synchronized(lock) {
            holder == thread
        }
    }

    /**
     * Reserves the critical section for the specified thread.
     *
     * If the section is not held, it will be reserved for the given thread.
     * If already reserved by the same thread, the operation succeeds.
     *
     * @param thread The thread to reserve the section for
     * @throws IllegalStateException if the critical section is already held by a different thread
     */
    @AnyThread
    fun reserveFor(thread: Thread) {
        synchronized(lock) {
            when (holder) {
                null -> holder = thread
                thread -> Unit
                else -> throw IllegalStateException("Critical section is held by $holder")
            }
        }
    }

    /**
     * Cancels the reservation of the critical section.
     *
     * This operation is only allowed if the critical section has not been entered yet.
     * Upon cancellation, all waiting threads are unparked.
     *
     * @throws IllegalStateException if the critical section has already been entered
     */
    @AnyThread
    fun cancelReservation() {
        synchronized(lock) {
            if (entranceCounter > 0) {
                throw IllegalStateException("Cannot cancel reservation: critical section has already been entered")
            }

            holder = null

            waiters.toList().forEach { waiter ->
                LockSupport.unpark(waiter)
            }
        }
    }

    /**
     * Enters the critical section.
     *
     * If the section is not held, the current thread becomes the holder.
     * If already held by the current thread, the entrance counter is incremented (reentrancy).
     * If held by another thread, the current thread will be parked until the section is released.
     *
     * @return A [Disposable] handle that must be passed to [exit] to leave the critical section
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @AnyThread
    @Throws(InterruptedException::class)
    fun enter(): Disposable {
        val currentThread = Thread.currentThread()

        while (true) {
            synchronized(lock) {
                when (holder) {
                    null -> {
                        holder = currentThread
                        entranceCounter = 1
                        return EntranceHandle(this)
                    }

                    currentThread -> {
                        entranceCounter++
                        return EntranceHandle(this)
                    }

                    else -> {
                        if (currentThread !in waiters) {
                            waiters.add(currentThread)
                        }
                    }
                }
            }

            LockSupport.park()

            synchronized(lock) {
                waiters.remove(currentThread)
            }

            if (Thread.interrupted()) {
                Thread.currentThread().interrupt()
                throw InterruptedException()
            }
        }
    }

    /**
     * Decrements the entrance counter, and if it reaches zero,
     * releases the critical section and unparks waiting threads.
     *
     * **NOTE:** The handle must be the one returned by [enter].
     *
     * @param handle The entrance handle returned by [enter]
     * @throws IllegalArgumentException if the provided handle has unknown origin
     */
    @AnyThread
    fun exit(handle: Disposable) {
        if (handle !is EntranceHandle) {
            throw IllegalArgumentException("Incorrect entrance handle: $handle")
        }
        handle.close()
    }

    private class EntranceHandle(
        private val criticalSection: BindingCriticalSection,
    ) : Disposable {

        private var closed = false

        @AnyThread
        override fun close(): Unit = synchronized(criticalSection.lock) {
            if (closed) {
                return
            }
            closed = true

            criticalSection.apply {
                if (entranceCounter == 0) {
                    throw IllegalStateException("Critical section is not held")
                }

                entranceCounter--
                if (entranceCounter == 0) {
                    holder = null

                    waiters.toList().forEach { waiter ->
                        LockSupport.unpark(waiter)
                    }
                }
            }
        }
    }
}

internal inline fun BindingCriticalSection.runBindingAction(crossinline action: () -> Unit) {
    if (UiThreadHandler.isMainThread()) {
        action()
    } else {
        postBindingAction(action)
    }
}

internal inline fun BindingCriticalSection.postBindingAction(crossinline action: () -> Unit) {
    val handle = enter()
    var posted = false

    try {
        UiThreadHandler.get().post {
            try {
                action()
            } finally {
                exit(handle)
            }
        }
        posted = true
    } finally {
        if (!posted) {
            exit(handle)
        }
    }
}
