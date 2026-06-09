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
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.CountDownLatch
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
}
