package com.yandex.div.core.data

import com.yandex.div.core.expression.RuntimeStoreProvider
import com.yandex.div.core.expression.local.RuntimeStoreImpl
import com.yandex.div.core.view2.Div2View
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.locks.ReentrantLock
import kotlin.test.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

internal class DivDataViewConnectorTest {

    private val store = mock<RuntimeStoreImpl>()
    private val runtimeStoreProvider = mock<RuntimeStoreProvider> {
        on { store } doReturn store
    }
    private val connector = DivDataViewConnector(runtimeStoreProvider)
    private val view = mock<Div2View>()
    private val anotherView = mock<Div2View>()

    @Test
    fun `attach view to runtime store`() {
        connector.attach(view)

        verify(store).attachView(view)
    }

    @Test
    fun `track view when runtime store is unavailable`() {
        val runtimeStoreProvider = mock<RuntimeStoreProvider>()
        val connector = DivDataViewConnector(runtimeStoreProvider)
        connector.attach(view)
        whenever(runtimeStoreProvider.store).thenReturn(store)

        connector.detach(view)

        verify(store).clearBindings(view)
    }

    @Test
    fun `ignore detach when view is not attached`() {
        connector.detach(view)

        verifyNoInteractions(store)
    }

    @Test
    fun `remove view when runtime store is unavailable`() {
        connector.attach(view)
        whenever(runtimeStoreProvider.store).thenReturn(null)
        connector.detach(view)
        val restoredStore = mock<RuntimeStoreImpl>()
        whenever(runtimeStoreProvider.store).thenReturn(restoredStore)

        connector.detach(view)

        verifyNoInteractions(restoredStore)
    }

    @Test
    fun `keep runtimes when another view remains attached`() {
        connector.attach(view)
        connector.attach(anotherView)

        connector.detach(view)

        verify(store).clearBindings(view)
        verify(store, never()).cleanupRuntimes(any())
    }

    @Test
    fun `cleanup runtimes when last view detaches`() {
        connector.attach(view)

        connector.detach(view)

        verify(store).cleanupRuntimes(view)
    }

    @Test
    fun `cleanup runtimes when remaining view detaches`() {
        connector.attach(view)
        connector.attach(anotherView)
        connector.detach(view)

        connector.detach(anotherView)

        verify(store).cleanupRuntimes(anotherView)
    }

    @Test
    fun `attach view after concurrent cleanup completes`() {
        runAttachBlockedByCleanup()

        verify(store).attachView(anotherView)
    }

    @Test
    fun `do not attach view while cleanup is in progress`() {
        runAttachBlockedByCleanup {
            verify(store, never()).attachView(anotherView)
        }
    }

    private fun runAttachBlockedByCleanup(assertWhileBlocked: () -> Unit = {}) {
        val cleanupStarted = CountDownLatch(1)
        val releaseCleanup = CountDownLatch(1)
        val attachAttempted = CountDownLatch(1)
        var observeLockAttempt = false
        val lock = object : ReentrantLock() {
            override fun lock() {
                if (observeLockAttempt) {
                    attachAttempted.countDown()
                }
                super.lock()
            }
        }
        val connector = DivDataViewConnector(runtimeStoreProvider, lock)
        doAnswer {
            cleanupStarted.countDown()
            releaseCleanup.await()
        }.whenever(store).cleanupRuntimes(view)
        connector.attach(view)
        val detachTask = FutureTask {
            try {
                connector.detach(view)
            } finally {
                cleanupStarted.countDown()
            }
        }
        val attachTask = FutureTask {
            try {
                connector.attach(anotherView)
            } finally {
                attachAttempted.countDown()
            }
        }
        val detachThread = Thread(detachTask)
        val attachThread = Thread(attachTask)

        try {
            detachThread.start()
            cleanupStarted.await()
            observeLockAttempt = true
            attachThread.start()
            attachAttempted.await()
            assertWhileBlocked()
        } finally {
            releaseCleanup.countDown()
            detachThread.join()
            attachThread.join()
        }
        detachTask.get()
        attachTask.get()
    }
}
