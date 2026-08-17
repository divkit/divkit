package com.yandex.div.core.util.binding

import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.view2.Div2View
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Integration tests for [BindingDispatcher].
 *
 * Pins the contract for the two SDK changes:
 *   1. `withLock` uses [BindingCriticalSection.tryEnter] on the main thread, so the main thread
 *      never parks indefinitely during contention.
 *   2. `runOnBindingThread(onComplete, ...)` calls [BindingCriticalSection.transferToCurrentThread]
 *      before invoking onComplete, so a `setData` (i.e. `withLock`) call triggered inside the
 *      callback is NOT silently dropped. Without the transfer, holder would still be the
 *      binding thread and `withLock` would return the fallback.
 *
 * The tests run under [RobolectricTestRunner] so [com.yandex.div.internal.util.UiThreadHandler]
 * uses a real Robolectric main looper (different from the binding thread). This is what makes
 * the transfer behavior observable — under [TestUiThreadHandler], every thread is treated as
 * the main thread, which would mask the bug.
 */
@RunWith(RobolectricTestRunner::class)
internal class BindingDispatcherTest {

    // reportLockFail() invokes KAssert.fail. With asserts enabled it would throw
    // AssertionError and break the ANR-regression test, which expects withLock to
    // return the fallback gracefully when contended.
    @get:Rule
    val disableAsserts = EnableAssertsRule(enable = false)

    private val divView = mock<Div2View>()
    private val criticalSection = BindingCriticalSection()
    private val executor = BindingThreadExecutor.create("test-binding-thread")
    private val dispatcher = BindingDispatcher(divView, criticalSection, executor)

    @After
    fun tearDown() {
        // Drain any pending main-thread work so it does not leak into the next test.
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
    }

    /**
     * `withLock` on a free section must execute the block and return its result.
     */
    @Test
    fun `withLock executes block on free section`() {
        val result = dispatcher.withLock(fallback = -1) { 42 }

        assertEquals(42, result)
        assertFalse(criticalSection.isHeld)
    }

    /**
     * `withLock` must return the fallback (and NOT park) when another thread holds the section.
     * This is the regression test for the original ANR.
     */
    @Test(timeout = 5_000)
    fun `withLock returns fallback when another thread holds the lock instead of parking`() {
        val acquired = CountDownLatch(1)
        val release = CountDownLatch(1)

        val owner = Thread {
            val handle = criticalSection.enter()
            acquired.countDown()
            release.await()
            criticalSection.exit(handle)
        }
        owner.start()
        acquired.await()

        // Would have parked indefinitely under the old check-then-enter implementation.
        val result = dispatcher.withLock(fallback = "fallback") { "executed" }
        assertEquals("fallback", result)

        release.countDown()
        owner.join()
    }

    /**
     * Core regression test for the fix in `runOnBindingThread`: when onComplete runs on the
     * main thread, [BindingCriticalSection.transferToCurrentThread] must have moved holder
     * from the binding thread to the main thread. Otherwise a reentrant `withLock` from inside
     * onComplete sees `holder == bindingThread`, [BindingCriticalSection.tryEnter] returns null,
     * and the reentrant block is silently replaced with the fallback.
     *
     * The block runs on the binding thread; onComplete is posted to the Robolectric main
     * looper, so we drive it forward with [ShadowLooper.runUiThreadTasksIncludingDelayedTasks].
     */
    @Test(timeout = 5_000)
    fun `withLock inside onComplete executes its block — proves transferToCurrentThread happened`() {
        val blockThread = AtomicReference<Thread>()
        val onCompleteThread = AtomicReference<Thread>()
        val reentrantOutcome = AtomicReference<String>("not-set")
        val reentrantBlockRan = AtomicInteger(0)
        val onCompleteRan = CountDownLatch(1)

        dispatcher.runOnBindingThread<Unit>(
            onComplete = { _ ->
                onCompleteThread.set(Thread.currentThread())

                // This is the path that was silently dropped before the fix.
                val result = dispatcher.withLock(fallback = "DROPPED") {
                    reentrantBlockRan.incrementAndGet()
                    "executed"
                }
                reentrantOutcome.set(result)

                onCompleteRan.countDown()
            }
        ) {
            blockThread.set(Thread.currentThread())
        }

        // Drive the main looper so onComplete actually fires.
        // Loop a few times because tasks may post follow-ups.
        repeat(10) {
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            if (onCompleteRan.count == 0L) return@repeat
            Thread.sleep(20)
        }
        assertTrue("onComplete never fired", onCompleteRan.await(2, TimeUnit.SECONDS))

        // Sanity: block ran on a real binding thread, onComplete on the main thread.
        assertNotNull(blockThread.get())
        assertNotNull(onCompleteThread.get())
        assertTrue(
            "binding thread and main thread should be different",
            blockThread.get() != onCompleteThread.get()
        )

        assertEquals(
            "Reentrant withLock inside onComplete MUST execute its block. " +
                "Got fallback instead — this means transferToCurrentThread() was missing.",
            "executed",
            reentrantOutcome.get()
        )
        assertEquals(1, reentrantBlockRan.get())

        // Drain any trailing posts and verify clean unwind.
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertFalse("Section must be released after onComplete unwinds", criticalSection.isHeld)
        assertFalse(criticalSection.isReserved)
    }

    /**
     * Multiple sequential `withLock` calls inside onComplete must all execute their block —
     * the transferred ownership stays with the main thread for the entire callback.
     */
    @Test(timeout = 5_000)
    fun `multiple sequential withLock calls inside onComplete all succeed`() {
        val successes = AtomicInteger(0)
        val done = CountDownLatch(1)

        dispatcher.runOnBindingThread<Unit>(
            onComplete = { _ ->
                repeat(3) {
                    dispatcher.withLock(fallback = false) {
                        successes.incrementAndGet()
                        true
                    }
                }
                done.countDown()
            }
        ) {
            // no-op binding work
        }

        repeat(10) {
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            if (done.count == 0L) return@repeat
            Thread.sleep(20)
        }
        assertTrue(done.await(2, TimeUnit.SECONDS))
        assertEquals(3, successes.get())

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertFalse(criticalSection.isHeld)
    }

    /**
     * `runOnBindingThread` without onComplete must release the lock from the binding thread
     * (the legacy path). Verifies the fix didn't break the no-callback branch.
     */
    @Test(timeout = 5_000)
    fun `runOnBindingThread without onComplete releases lock from binding thread`() {
        val ran = CountDownLatch(1)

        dispatcher.runOnBindingThread<Unit>(onComplete = null) {
            ran.countDown()
        }

        assertTrue(ran.await(3, TimeUnit.SECONDS))

        // The exit happens in the binding thread's finally block right after our block returns;
        // give it a brief moment to land before asserting.
        val deadline = System.currentTimeMillis() + 1_000
        while (criticalSection.isHeld && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
        assertFalse("Section must be released even without onComplete", criticalSection.isHeld)
        assertFalse(criticalSection.isReserved)

        // And the section must be reusable afterwards.
        val handle = criticalSection.tryEnter()
        assertNotNull(handle)
        criticalSection.exit(handle!!)
    }

    @Test(timeout = 5_000)
    fun `pending task does not block shared executor while previous task waits for main thread`() {
        val sharedExecutor = BindingThreadExecutor.create("shared-test-binding-thread")
        val firstDispatcher = BindingDispatcher(mock(), BindingCriticalSection(), sharedExecutor)
        val secondDispatcher = BindingDispatcher(mock(), BindingCriticalSection(), sharedExecutor)
        val firstBackgroundPhaseFinished = CountDownLatch(1)
        val firstPendingTaskFinished = CountDownLatch(1)
        val secondDispatcherTaskFinished = CountDownLatch(1)

        firstDispatcher.runOnBindingThread<Unit>(onComplete = {}) {
            firstBackgroundPhaseFinished.countDown()
        }
        assertTrue(firstBackgroundPhaseFinished.await(2, TimeUnit.SECONDS))

        firstDispatcher.runOnBindingThread<Unit> {
            firstPendingTaskFinished.countDown()
        }
        secondDispatcher.runOnBindingThread<Unit> {
            secondDispatcherTaskFinished.countDown()
        }

        assertTrue(
            "A pending task of one dispatcher must not occupy the shared binding thread",
            secondDispatcherTaskFinished.await(2, TimeUnit.SECONDS)
        )
        assertEquals(1L, firstPendingTaskFinished.count)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertTrue(firstPendingTaskFinished.await(2, TimeUnit.SECONDS))
    }

    @Test(timeout = 5_000)
    fun `tasks of one dispatcher execute in fifo order`() {
        val executionOrder = CopyOnWriteArrayList<Int>()
        val firstBackgroundPhaseFinished = CountDownLatch(1)
        val allTasksFinished = CountDownLatch(1)

        dispatcher.runOnBindingThread<Unit>(onComplete = {}) {
            executionOrder += 1
            firstBackgroundPhaseFinished.countDown()
        }
        assertTrue(firstBackgroundPhaseFinished.await(2, TimeUnit.SECONDS))

        dispatcher.runOnBindingThread<Unit> {
            executionOrder += 2
        }
        dispatcher.runOnBindingThread<Unit> {
            executionOrder += 3
            allTasksFinished.countDown()
        }

        assertEquals(listOf(1), executionOrder)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertTrue(allTasksFinished.await(2, TimeUnit.SECONDS))
        assertEquals(listOf(1, 2, 3), executionOrder)
    }

    @Test(timeout = 5_000)
    fun `executor rejection drains queue iteratively and reports every error`() {
        val rejectedExecutor = mock<BindingThreadExecutor>()
        val rejectedDivView = mock<Div2View>()
        val rejectedDispatcher = BindingDispatcher(
            rejectedDivView,
            BindingCriticalSection(),
            rejectedExecutor,
        )
        val firstTask = AtomicReference<Runnable>()
        val executeCount = AtomicInteger()
        val errorCount = AtomicInteger()
        val pendingTaskCount = 2_000
        whenever(rejectedExecutor.ensureThreadCreated()).thenReturn(Thread.currentThread())
        doAnswer { invocation ->
            if (executeCount.getAndIncrement() == 0) {
                firstTask.set(invocation.getArgument(0))
            } else {
                throw RejectedExecutionException("rejected")
            }
            Unit
        }.whenever(rejectedExecutor).execute(any())

        rejectedDispatcher.runOnBindingThread<Unit> { Unit }
        repeat(pendingTaskCount) {
            rejectedDispatcher.runOnBindingThread<Unit>(onError = { errorCount.incrementAndGet() }) { Unit }
        }

        firstTask.get().run()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertEquals(pendingTaskCount, errorCount.get())
        verify(rejectedDivView, times(pendingTaskCount)).logError(any())
    }

    @Test(timeout = 5_000)
    fun `cancelPendingTasks removes only this dispatcher's queued task`() {
        val blockerStarted = CountDownLatch(1)
        val releaseBlocker = CountDownLatch(1)
        executor.execute {
            blockerStarted.countDown()
            releaseBlocker.await()
        }
        assertTrue(blockerStarted.await(1, TimeUnit.SECONDS))
        val mainThreadHandle = criticalSection.enter()

        val cancelledRuns = AtomicInteger()
        repeat(100) {
            dispatcher.runOnBindingThread<Unit> {
                cancelledRuns.incrementAndGet()
            }
        }

        val otherDispatcher = BindingDispatcher(divView, BindingCriticalSection(), executor)
        val otherRun = CountDownLatch(1)
        otherDispatcher.runOnBindingThread<Unit> {
            otherRun.countDown()
        }

        assertEquals(2, executor.queueSize)
        dispatcher.cancelPendingTasks()
        assertEquals(1, executor.queueSize)
        assertTrue(criticalSection.isHeld)
        assertFalse(criticalSection.isReserved)
        mainThreadHandle.close()

        releaseBlocker.countDown()
        assertTrue(otherRun.await(2, TimeUnit.SECONDS))
        assertEquals(0, cancelledRuns.get())
    }

    @Test(timeout = 5_000)
    fun `cancelPendingTasks ignores stale main thread actions and completion`() {
        val backgroundStarted = CountDownLatch(1)
        val releaseBackground = CountDownLatch(1)
        val backgroundFinished = CountDownLatch(1)
        val deferredRuns = AtomicInteger()
        val completionRuns = AtomicInteger()

        dispatcher.runOnBindingThread<Unit>(onComplete = { completionRuns.incrementAndGet() }) {
            dispatcher.runMainThreadAction { deferredRuns.incrementAndGet() }
            backgroundStarted.countDown()
            releaseBackground.await()
            backgroundFinished.countDown()
        }
        assertTrue(backgroundStarted.await(1, TimeUnit.SECONDS))

        dispatcher.cancelPendingTasks()
        releaseBackground.countDown()
        assertTrue(backgroundFinished.await(1, TimeUnit.SECONDS))
        repeat(10) {
            Thread.sleep(20)
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        }

        assertEquals(0, deferredRuns.get())
        assertEquals(0, completionRuns.get())
        assertFalse(criticalSection.isHeld)
        assertFalse(criticalSection.isReserved)
    }

    @Test(timeout = 5_000)
    fun `cancelPendingTasks logs stale background error without invoking callback`() {
        val backgroundStarted = CountDownLatch(1)
        val releaseBackground = CountDownLatch(1)
        val backgroundFinished = CountDownLatch(1)
        val errorLogged = CountDownLatch(1)
        val expectedError = RuntimeException("background failure")
        val errorCallbackRuns = AtomicInteger()
        doAnswer { errorLogged.countDown() }.whenever(divView).logError(expectedError)

        dispatcher.runOnBindingThread<Unit>(onError = { errorCallbackRuns.incrementAndGet() }) {
            backgroundStarted.countDown()
            try {
                releaseBackground.await()
                throw expectedError
            } finally {
                backgroundFinished.countDown()
            }
        }
        assertTrue(backgroundStarted.await(1, TimeUnit.SECONDS))

        dispatcher.cancelPendingTasks()
        releaseBackground.countDown()
        assertTrue(backgroundFinished.await(1, TimeUnit.SECONDS))
        val deadline = System.currentTimeMillis() + 2_000
        while (errorLogged.count > 0 && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        }

        assertTrue("Background error was not logged", errorLogged.await(0, TimeUnit.MILLISECONDS))
        verify(divView).logError(expectedError)
        assertEquals(0, errorCallbackRuns.get())
        assertFalse(criticalSection.isHeld)
        assertFalse(criticalSection.isReserved)
    }
}
