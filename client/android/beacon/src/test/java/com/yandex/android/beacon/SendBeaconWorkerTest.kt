package com.yandex.android.beacon

import android.net.Uri
import com.yandex.android.TestExecutor
import com.yandex.android.beacon.BeaconItem.Persistent
import com.yandex.div.internal.util.Clock
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class SendBeaconWorkerTest {

    private val context = RuntimeEnvironment.application
    private val requestExecutor = TestSendBeaconRequestExecutor()
    private val executor = TestExecutor()
    private val hostCallback = mock<SendBeaconWorkerScheduler>()
    private val logger = mock<SendBeaconPerWorkerLogger>()

    private val goUrl = mock<Uri> { on { host } doReturn GO_DOMAIN }
    private val noGoUrl = mock<Uri> { on { host } doReturn NO_GO_DOMAIN }

    private var now = 0L
    private val realClock = Clock.get()
    private val mockClock = mock<Clock> { on { currentTimeMs } doAnswer { now } }

    private var testDb = TestSendBeaconDb(context)
    private val realDbFactory = SendBeaconDb.factory
    private val testDbFactory = SendBeaconDb.Factory { _, _ -> testDb }

    private lateinit var worker: SendBeaconWorkerImpl

    @Before
    fun setUp() {
        SendBeaconDb.factory = testDbFactory
        Clock.setForTests(mockClock)

        worker = SendBeaconWorkerImpl(
            context = context,
            SendBeaconConfiguration(
                requestExecutor = requestExecutor,
                executor = executor,
                workerScheduler = hostCallback,
                perWorkerLogger = logger,
                databaseName = TestSendBeaconDb.DB_NAME
            )
        )
    }

    @After
    fun tearDown() {
        SendBeaconDb.factory = realDbFactory
        Clock.setForTests(realClock)
    }

    @Test
    fun `try immediately`() {
        worker.add(goUrl, emptyMap(), null, true)

        assertFalse(executor.commands.isEmpty())
        assertTrue(requestExecutor.sentOut.isEmpty())

        executor.runAll()

        assertTrue(requestExecutor.sentOut.contains(goUrl))
        assertTrue(testDb.items.isEmpty())
        verify(hostCallback, never()).schedule(eq(worker))
    }

    @Test
    fun `schedule for service`() {
        requestExecutor.badDomains.add(NO_GO_DOMAIN)

        worker.add(goUrl, emptyMap(), null, false)
        worker.add(noGoUrl, emptyMap(), null, false)

        assertFalse(executor.commands.isEmpty())
        assertTrue(requestExecutor.sentOut.isEmpty())
        verify(hostCallback, never()).schedule(eq(worker))

        executor.runAllOnce()

        assertEquals(2, testDb.items.size)
        verify(hostCallback, times(2)).schedule(eq(worker))

        val job1Callback = mock<SendBeaconWorker.Callback>()
        var startResult = worker.onStart(job1Callback)
        assertTrue(startResult)

        requestExecutor.ioException = IOException("No network")
        executor.runAllOnce()

        assertFalse(requestExecutor.sentOut.contains(goUrl))
        assertEquals(2, testDb.items.size)
        verify(job1Callback, times(1)).finish(true)

        val job2Callback = mock<SendBeaconWorker.Callback>()
        startResult = worker.onStart(job2Callback)
        assertTrue(startResult)

        requestExecutor.ioException = null
        executor.runAllOnce()

        assertEquals(1, requestExecutor.sentOut.size)
        assertEquals(1, testDb.items.size)

        // Sometimes we call it, sometimes not. Imitating race conditions.
        var stopResult = worker.onStop()
        assertTrue(stopResult)

        verify(job2Callback, times(1)).finish(true)
        verify(job2Callback, times(1)).finish(anyBoolean())

        val job3Callback = mock<SendBeaconWorker.Callback>()
        startResult = worker.onStart(job3Callback)
        assertTrue(startResult)

        requestExecutor.badDomains.clear()
        executor.runAllOnce()

        assertEquals(2, requestExecutor.sentOut.size)
        assertEquals(0, testDb.items.size)
        verify(job3Callback, times(1)).finish(false)
        verify(job3Callback, times(1)).finish(anyBoolean())

        stopResult = worker.onStop()
        assertFalse(stopResult)

        assertTrue(requestExecutor.sentOut.contains(noGoUrl))
        verify(job1Callback, times(1)).finish(anyBoolean())
    }

    @Test
    fun `start stop start`() {
        worker.add(goUrl, emptyMap(), null, false)
        worker.add(noGoUrl, emptyMap(), null, false)

        assertFalse(executor.commands.isEmpty())
        assertTrue(requestExecutor.sentOut.isEmpty())
        verify(hostCallback, never()).schedule(eq(worker))

        executor.runAllOnce()

        assertEquals(2, testDb.items.size)
        verify(hostCallback, times(2)).schedule(eq(worker))

        val job1Callback = mock<SendBeaconWorker.Callback>()
        var startResult = worker.onStart(job1Callback)
        assertTrue(startResult)

        val stopResult = worker.onStop()
        assertTrue(stopResult)

        executor.runAllOnce()

        assertTrue(requestExecutor.sentOut.isEmpty())
        assertEquals(2, testDb.items.size)
        verify(job1Callback, never()).finish(anyBoolean())

        val job2Callback = mock<SendBeaconWorker.Callback>()
        startResult = worker.onStart(job2Callback)
        assertTrue(startResult)

        requestExecutor.postSendHook = Runnable { worker.onStop() }
        executor.runAllOnce()

        assertEquals(1, requestExecutor.sentOut.size)
        assertEquals(1, testDb.items.size)
        verify(job1Callback, never()).finish(anyBoolean())
        verify(job2Callback, never()).finish(anyBoolean())
    }

    @Test
    fun `after restart and expire`() {
        val baselineTime = 200000L
        val expirePeriodMs = SendBeaconWorkerImpl.URL_EXPIRE_PERIOD_MS

        testDb.items.apply {
            add(Persistent(goUrl, emptyMap(), null, baselineTime, 0))
            add(Persistent(noGoUrl, emptyMap(), null, baselineTime + expirePeriodMs / 2, 1))
        }

        now = baselineTime + (expirePeriodMs * 0.75).toLong()

        requestExecutor.ioException = IOException("No network")

        val job1Callback = mock<SendBeaconWorker.Callback>()
        var startResult = worker.onStart(job1Callback)
        assertTrue(startResult)

        executor.runAllOnce()

        assertEquals(2, testDb.items.size)
        verify(job1Callback, times(1)).finish(true)
        verify(job1Callback, times(1)).finish(anyBoolean())

        now = baselineTime + (expirePeriodMs * 1.25).toLong()

        val job2Callback = mock<SendBeaconWorker.Callback>()
        startResult = worker.onStart(job2Callback)
        assertTrue(startResult)

        executor.runAllOnce()

        assertEquals(1, testDb.items.size)
        verify(job2Callback, times(1)).finish(true)
        verify(job2Callback, times(1)).finish(anyBoolean())

        requestExecutor.ioException = null

        val job3Callback = mock<SendBeaconWorker.Callback>()
        startResult = worker.onStart(job3Callback)
        assertTrue(startResult)

        executor.runAllOnce()

        verify(job3Callback, times(1)).finish(false)
        verify(job3Callback, times(1)).finish(anyBoolean())

        val job4Callback = mock<SendBeaconWorker.Callback>()
        startResult = worker.onStart(job4Callback)
        assertFalse(startResult)

        assertTrue(executor.commands.isEmpty())
        verify(job4Callback, times(0)).finish(anyBoolean())
    }

    private companion object {

        private const val GO_DOMAIN = "yandex.ru"
        private const val NO_GO_DOMAIN = "yandex.nogo"
    }
}
