package com.yandex.div.core.view2

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivExtension
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewLegacyBindingCompatibilityTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val worker = ControlledBindingExecutor()
    private val activity = lazy { Robolectric.buildActivity(Activity::class.java).setup() }
    private val tag = DivDataTag("legacy-binding")
    private val card = data(text(text = "Content"))
    private val extendedCard = data(text(text = "Content", extensions = listOf(DivExtension(id = "media"))))
    private val extension = mock<DivExtensionHandler> {
        on { matches(any()) } doReturn true
    }
    private val view = createView()

    @AfterTest
    fun cleanup() {
        try {
            worker.settle()
            view.cleanup()
        } finally {
            worker.close()
            if (activity.isInitialized()) activity.value.pause().stop().destroy()
        }
    }

    @Test
    fun `idle completion callback propagates its original exception`() {
        view.setData(card, tag)
        val failure = IllegalStateException("Completion failed")

        val thrown = runCatching {
            view.setDataAsync(card, tag, onComplete = { throw failure })
        }.exceptionOrNull()

        assertSame(failure, thrown)
    }

    @Test
    fun `idle completion callback propagates its original exception with old data`() {
        view.setData(card, tag)
        val failure = IllegalStateException("Completion failed")

        val thrown = runCatching {
            view.setDataAsync(card, oldDivData = card, tag = tag, onComplete = { throw failure })
        }.exceptionOrNull()

        assertSame(failure, thrown)
    }

    @Test
    fun `idle media failure propagates without invoking completion`() {
        prepareMediaReload()
        val failure = IllegalStateException("Media reload failed")
        doThrow(failure).whenever(extension).loadMedia(eq(view), any(), any(), any())
        val results = mutableListOf<Boolean>()

        val thrown = runCatching {
            view.setDataAsync(extendedCard, tag, onComplete = { results += it })
        }.exceptionOrNull()

        assertEquals<Pair<List<Boolean>, Throwable?>>(emptyList<Boolean>() to failure, results.toList() to thrown)
    }

    @Test
    fun `idle media failure propagates without invoking completion with old data`() {
        prepareMediaReload()
        val failure = IllegalStateException("Media reload failed")
        doThrow(failure).whenever(extension).loadMedia(eq(view), any(), any(), any())
        val results = mutableListOf<Boolean>()

        val thrown = runCatching {
            view.setDataAsync(extendedCard, oldDivData = extendedCard, tag = tag, onComplete = { results += it })
        }.exceptionOrNull()

        assertEquals<Pair<List<Boolean>, Throwable?>>(emptyList<Boolean>() to failure, results.toList() to thrown)
    }

    @Test
    fun `queued completion callback failure retains the legacy false callback`() {
        val failure = IllegalStateException("Completion failed")
        val results = mutableListOf<Boolean>()

        view.setDataAsync(card, tag, onComplete = {
            results += it
            if (it) throw failure
        })
        worker.settle()

        assertEquals(listOf(true, false), results)
    }

    @Test
    fun `queued completion callback failure retains the legacy false callback with old data`() {
        val failure = IllegalStateException("Completion failed")
        val results = mutableListOf<Boolean>()

        view.setDataAsync(card, oldDivData = null, tag = tag, onComplete = {
            results += it
            if (it) throw failure
        })
        worker.settle()

        assertEquals(listOf(true, false), results)
    }

    @Test
    fun `queued failure callback propagates its original exception`() {
        view.addPersistentDivDataObserver(mock {
            on { onBeforeDivDataChanged() } doThrow IllegalArgumentException("Binding failed")
        })
        val failure = IllegalStateException("Failure callback failed")
        view.setDataAsync(card, tag, onComplete = { throw failure })
        worker.runWorker()

        val thrown = runCatching { ShadowLooper.idleMainLooper() }.exceptionOrNull()

        assertSame(failure, thrown)
    }

    @Test
    fun `queued failure callback propagates its original exception with old data`() {
        view.addPersistentDivDataObserver(mock {
            on { onBeforeDivDataChanged() } doThrow IllegalArgumentException("Binding failed")
        })
        val failure = IllegalStateException("Failure callback failed")
        view.setDataAsync(card, oldDivData = null, tag = tag, onComplete = { throw failure })
        worker.runWorker()

        val thrown = runCatching { ShadowLooper.idleMainLooper() }.exceptionOrNull()

        assertSame(failure, thrown)
    }

    @Test
    fun `queued same data without a callback finishes on worker`() {
        view.setData(card, tag)
        view.viewComponent.bindingDispatcher.runOnBindingThread(BindingTaskType.DATA) { Unit }

        view.setDataAsync(card, tag, onComplete = null)
        worker.runWorker()

        assertFalse(view.viewComponent.bindingDispatcher.hasPendingAsyncBindings)
    }

    @Test
    fun `queued same data without a callback finishes on worker with old data`() {
        view.setData(card, tag)
        view.viewComponent.bindingDispatcher.runOnBindingThread(BindingTaskType.DATA) { Unit }

        view.setDataAsync(card, oldDivData = card, tag = tag, onComplete = null)
        worker.runWorker()

        assertFalse(view.viewComponent.bindingDispatcher.hasPendingAsyncBindings)
    }

    @Test
    fun `idle completion stays inline on its background caller`() {
        view.setData(card, tag)
        val results = mutableListOf<Pair<Boolean, Thread?>>()

        worker.executor.execute {
            view.setDataAsync(card, tag, onComplete = { results += it to Thread.currentThread() })
        }
        worker.runWorker()

        assertEquals(listOf(true to worker.executor.bindingThread), results)
    }

    @Test
    fun `idle completion stays inline on its background caller with old data`() {
        view.setData(card, tag)
        val results = mutableListOf<Pair<Boolean, Thread?>>()

        worker.executor.execute {
            view.setDataAsync(card, oldDivData = card, tag = tag, onComplete = {
                results += it to Thread.currentThread()
            })
        }
        worker.runWorker()

        assertEquals(listOf(true to worker.executor.bindingThread), results)
    }

    @Test
    fun `cleanup preserves prepared legacy binding on attachment`() {
        view.setDataAsync(extendedCard, tag, onComplete = null)
        worker.settle()

        view.cleanup()
        attachView()
        worker.settle()

        verify(extension).bindView(eq(view), any(), any(), any())
    }

    @Test
    fun `cleanup preserves prepared legacy binding on attachment with old data`() {
        view.setDataAsync(extendedCard, oldDivData = null, tag = tag, onComplete = null)
        worker.settle()

        view.cleanup()
        attachView()
        worker.settle()

        verify(extension).bindView(eq(view), any(), any(), any())
    }

    private fun attachView() {
        activity.value.get().setContentView(view)
        activity.value.visible()
    }

    private fun prepareMediaReload() {
        attachView()
        view.setData(extendedCard, tag)
        view.releaseMedia()
    }

    private fun createView(): Div2View {
        val context = Div2Context(testContextThemeWrapper(),
            DivConfiguration.Builder(mock())
                .enableBindOnAttach(true)
                .extension(extension)
                .divErrorsReporter(mock())
                .build(),
        )
        val component = TestComponent(context.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(context.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), worker.executor) },
            ),
        )
        return Div2View(spy(context) { on { div2Component } doReturn component })
    }
}
