package com.yandex.div.core.util.binding

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.util.EnableAssertsRule
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class BindingDispatcherLockFailureTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val failure = IllegalStateException("Cannot enter binding critical section")
    private val worker = ControlledBindingExecutor()
    private val criticalSection = mock<BindingCriticalSection> {
        on { enter() } doThrow failure
    }
    private val dispatcher = BindingDispatcher(mock(), criticalSection, worker.executor)

    @AfterTest
    fun cleanup() {
        worker.close()
    }

    @Test
    fun `lock acquisition failure escapes a legacy binding request`() {
        dispatcher.runOnBindingThread(BindingTaskType.DATA, onError = {}) {}

        val thrown = runCatching { worker.runNext() }.exceptionOrNull()

        assertSame(failure, thrown)
    }

    @Test
    fun `lock acquisition failure escapes a request with cancellation reporting`() {
        dispatcher.runOnBindingThread(BindingTaskType.DATA, onError = {}, onCancelled = {}) {}

        val thrown = runCatching { worker.runNext() }.exceptionOrNull()

        assertSame(failure, thrown)
    }

    @Test
    fun `queued legacy binding stays pending when preceding lock release fails`() {
        val releaseFailure = IllegalStateException("Cannot release binding critical section")
        val failingSection = mock<BindingCriticalSection> {
            on { enter() } doReturn mock()
            on { exit(any()) } doThrow releaseFailure
        }
        val dispatcher = BindingDispatcher(mock(), failingSection, worker.executor)
        var queuedBindingStarted = false
        dispatcher.runOnBindingThread(BindingTaskType.DATA, onComplete = {}) {}
        dispatcher.runOnBindingThread(BindingTaskType.DATA) { queuedBindingStarted = true }
        worker.runNext()

        runCatching { ShadowLooper.idleMainLooper() }
        runCatching { worker.runWorker() }

        assertFalse(queuedBindingStarted)
    }
}
