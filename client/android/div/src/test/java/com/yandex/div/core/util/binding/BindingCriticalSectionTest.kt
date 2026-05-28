package com.yandex.div.core.util.binding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Unit tests for [BindingCriticalSection].
 *
 * Focus: the cross-thread transfer + reentrant path introduced when
 * [BindingDispatcher.runOnBindingThread] calls [transferToCurrentThread] before invoking
 * onComplete on the main thread. Without the transfer, a reentrant `setData` triggered
 * inside onComplete would observe `holder == bindingThread` and either be silently dropped
 * by `withLock` or — historically, before the tryEnter-based fix — park the main thread
 * indefinitely.
 */
internal class BindingCriticalSectionTest {

    private val section = BindingCriticalSection()

    // region tryEnter — each branch of the new method

    @Test
    fun `tryEnter on free section acquires lock`() {
        val handle = section.tryEnter()

        assertNotNull("Expected free section to be acquirable", handle)
        assertTrue(section.isHeld)
        assertTrue(section.isHeldBy(Thread.currentThread()))

        section.exit(handle!!)
        assertFalse(section.isHeld)
    }

    @Test
    fun `tryEnter is reentrant for current holder`() {
        val handle1 = section.tryEnter()!!
        val handle2 = section.tryEnter()

        assertNotNull("Reentrant tryEnter must succeed for current holder", handle2)

        // Releasing the inner handle keeps the lock held by the outer one.
        section.exit(handle2!!)
        assertTrue(section.isHeld)

        section.exit(handle1)
        assertFalse(section.isHeld)
    }

    @Test
    fun `tryEnter returns null when held by another thread`() {
        val acquired = CountDownLatch(1)
        val release = CountDownLatch(1)

        val owner = Thread {
            val handle = section.tryEnter()!!
            acquired.countDown()
            release.await()
            section.exit(handle)
        }
        owner.start()
        acquired.await()

        // Main thread must NOT block here — that was the original ANR.
        val handle = section.tryEnter()
        assertNull("tryEnter must return null when another thread holds the lock", handle)

        release.countDown()
        owner.join()
    }

    @Test
    fun `tryEnter returns null when reserved for another thread`() {
        val other = Thread { /* never runs body */ }
        section.reserveFor(other)

        val handle = section.tryEnter()
        assertNull(
            "tryEnter must not steal a lock reserved for a different thread — " +
                "doing so would let two threads execute the binding block concurrently",
            handle
        )

        section.cancelReservation()
    }

    @Test
    fun `tryEnter succeeds when reserved for current thread`() {
        section.reserveFor(Thread.currentThread())

        val handle = section.tryEnter()
        assertNotNull("Reservation for current thread must allow acquisition", handle)
        // Reservation must be cleared on successful acquisition (matches enter() semantics).
        assertFalse(section.isReserved)

        section.exit(handle!!)
    }

    // endregion

    // region transfer + reentrant path — the scenario added by BindingDispatcher.runOnBindingThread fix

    /**
     * Simulates the patched `runOnBindingThread(onComplete)` flow:
     *   1. binding thread reserveFor + enter
     *   2. binding thread executes block, posts to main
     *   3. main thread transferToCurrentThread, runs onComplete (which reentrantly enters the section), exits
     *
     * Verifies: holder ends as null, entranceCounter unwinds correctly, no thread is left parked.
     */
    @Test
    fun `transfer to main then reentrant enter then exit unwinds cleanly`() {
        val mainExecuted = AtomicBoolean(false)
        val reentrantExecuted = AtomicBoolean(false)

        // Step 1+2: binding thread enters the section.
        val bindingThread = Thread.currentThread() // simulate "binding thread" as current
        section.reserveFor(bindingThread)
        val handle = section.enter()
        assertTrue(section.isHeldBy(bindingThread))

        // Step 3: simulate post to main thread by running on a new thread that "becomes" the
        // main thread after transferToCurrentThread.
        val mainLatch = CountDownLatch(1)
        val mainThread = Thread {
            section.transferToCurrentThread()
            assertTrue(
                "After transfer, holder must be the (simulated) main thread",
                section.isHeldBy(Thread.currentThread())
            )

            // Reentrant tryEnter from inside onComplete — this is the path withLock takes.
            val innerHandle = section.tryEnter()
            assertNotNull(
                "Reentrant tryEnter from main thread must succeed after transfer",
                innerHandle
            )
            reentrantExecuted.set(true)
            section.exit(innerHandle!!)

            // Outer counter should still be 1 — outer handle still owns the lock.
            assertTrue(section.isHeld)

            mainExecuted.set(true)
            section.exit(handle)

            mainLatch.countDown()
        }
        mainThread.start()
        assertTrue("Main-thread simulation timed out", mainLatch.await(2, TimeUnit.SECONDS))
        mainThread.join()

        assertTrue(mainExecuted.get())
        assertTrue(reentrantExecuted.get())
        assertFalse("Lock must be fully released after onComplete unwinds", section.isHeld)
        assertFalse(section.isReserved)
    }

    /**
     * Without the [transferToCurrentThread] call — i.e. the OLD buggy path — a reentrant
     * tryEnter from a thread that is NOT the holder must return null. This pins the contract
     * that motivated the fix: if we forget to transfer, we MUST NOT silently acquire the
     * lock under a different identity.
     */
    @Test
    fun `reentrant tryEnter without transfer returns null on different thread`() {
        val handle = section.enter() // current thread holds it

        val outcome = AtomicReference<Any?>("not-set")
        val done = CountDownLatch(1)
        Thread {
            // Different thread — no transferToCurrentThread.
            outcome.set(section.tryEnter())
            done.countDown()
        }.start()
        assertTrue(done.await(2, TimeUnit.SECONDS))

        assertNull(
            "tryEnter from a non-holder thread must return null — never steal the lock",
            outcome.get()
        )

        section.exit(handle)
    }

    // endregion

    // region concurrency / liveness — ensure tryEnter never deadlocks the main thread

    @Test
    fun `tryEnter contention does not deadlock the calling thread`() {
        // Spawn N threads that all try to acquire+release in a tight loop. None should park.
        val workers = 8
        val iterations = 500
        val start = CountDownLatch(1)
        val finished = CountDownLatch(workers)
        val acquireCount = AtomicInteger(0)
        val rejectCount = AtomicInteger(0)

        repeat(workers) {
            Thread {
                start.await()
                repeat(iterations) {
                    val h = section.tryEnter()
                    if (h != null) {
                        acquireCount.incrementAndGet()
                        section.exit(h)
                    } else {
                        rejectCount.incrementAndGet()
                    }
                }
                finished.countDown()
            }.start()
        }

        start.countDown()
        // 5s is generous; tryEnter should never block, so this loop is CPU-bound.
        val finishedInTime = finished.await(5, TimeUnit.SECONDS)
        if (!finishedInTime) {
            fail("tryEnter contention deadlocked — some workers never finished")
        }

        assertEquals(workers * iterations, acquireCount.get() + rejectCount.get())
        assertFalse("Lock must end up released", section.isHeld)
    }

    @Test
    fun `transfer pattern survives interleaving with main thread tryEnter`() {
        // Models the real ANR scenario:
        //  - background thread holds the lock
        //  - main thread repeatedly calls tryEnter (would have parked under the old design)
        //  - background completes, posts to main, transfers, runs reentrant tryEnter
        val backgroundHolds = CountDownLatch(1)
        val mainProbed = CountDownLatch(1)
        val release = CountDownLatch(1)

        val bg = Thread {
            val h = section.enter()
            backgroundHolds.countDown()
            mainProbed.await()
            release.await()
            // Simulate post-to-main: transfer + exit on a different thread.
            // For this test, we just exit on the same thread — the contract checked here is
            // that main-thread tryEnter is non-blocking while bg holds.
            section.exit(h)
        }
        bg.start()
        backgroundHolds.await()

        val mainOutcome = section.tryEnter()
        assertNull(
            "Main thread tryEnter must return null instantly while background holds the lock",
            mainOutcome
        )
        mainProbed.countDown()
        release.countDown()
        bg.join()

        assertFalse(section.isHeld)
    }
}
