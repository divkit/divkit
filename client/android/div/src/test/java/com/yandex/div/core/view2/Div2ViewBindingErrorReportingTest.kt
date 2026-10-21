package com.yandex.div.core.view2

import android.app.Activity
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivExtension
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.same
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewBindingErrorReportingTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val worker = ControlledBindingExecutor()
    private val activity = lazy { Robolectric.buildActivity(Activity::class.java).setup() }
    private val tag = DivDataTag("original-request")
    private val card = data(text(text = "Content"))
    private val extendedCard = data(text(text = "Content", extensions = listOf(DivExtension(id = "error-reporting"))))
    private val extension = mock<DivExtensionHandler> {
        on { matches(any()) } doReturn true
    }
    private val errorsReporter = mock<DivErrorsReporter>()
    private val errors = argumentCaptor<Throwable>()
    private val view = createView()

    @AfterTest
    fun cleanup() {
        try {
            view.cleanup()
            worker.close()
        } finally {
            if (activity.isInitialized()) activity.value.pause().stop().destroy()
        }
    }

    @Test
    fun `null data reports a synthetic failure with the captured request context`() {
        view.setDataAsync(null, tag, onBindingPhase = {})
        worker.settle()

        assertEquals(
            "java.lang.IllegalStateException: Async binding failed: request=<identity>, " +
                "tag=original-request, generation=0, reason=NO_DATA",
            reportedFailure(),
        )
    }

    @Test
    fun `data without states reports the unprepared root and captured request context`() {
        view.setDataAsync(data(states = emptyList()), tag, onBindingPhase = {})
        worker.settle()

        assertEquals(
            "java.lang.IllegalStateException: Async binding failed: request=<identity>, " +
                "tag=original-request, generation=0, reason=ROOT_NOT_PREPARED",
            reportedFailure(),
        )
    }

    @Test
    fun `worker binding failure reports the original exception once`() {
        attachView()
        val failure = IllegalStateException("Worker binding failed")
        doThrow(failure).whenever(extension).bindView(eq(view), any(), any(), any())

        view.setDataAsync(extendedCard, tag, onBindingPhase = {})
        worker.settle()

        verify(errorsReporter, only()).onRuntimeError(eq(extendedCard), eq(tag), same(failure))
    }

    @Test
    fun `attachment binding failure reports the original exception once`() {
        val failure = IllegalStateException("Attachment binding failed")
        doThrow(failure).whenever(extension).bindView(eq(view), any(), any(), any())
        view.setDataAsync(extendedCard, tag, onBindingPhase = {})
        worker.settle()

        runCatching { attachView() }
        worker.settle()

        verify(errorsReporter, only()).onRuntimeError(eq(extendedCard), eq(tag), same(failure))
    }

    @Test
    fun `media reload failure reports the original exception once`() {
        attachView()
        view.setDataAsync(extendedCard, tag, onBindingPhase = {})
        worker.settle()
        view.releaseMedia()
        val failure = IllegalStateException("Media reload failed")
        doThrow(failure).whenever(extension).loadMedia(eq(view), any(), any(), any())

        runCatching { view.setDataAsync(extendedCard, tag, onBindingPhase = {}) }
        worker.settle()

        verify(errorsReporter, only()).onRuntimeError(eq(extendedCard), eq(tag), same(failure))
    }

    @Test
    fun `repeated cleanup does not report cancellation as an error`() {
        view.setDataAsync(card, tag, onBindingPhase = {})
        worker.settle()

        view.cleanup()
        view.cleanup()
        worker.settle()

        verifyNoInteractions(errorsReporter)
    }

    @Test
    fun `synchronous replacement does not report cancellation as an error`() {
        view.setDataAsync(card, tag, onBindingPhase = {})
        worker.settle()

        view.setData(data(text(text = "Replacement")), DivDataTag("replacement"))
        worker.settle()

        verifyNoInteractions(errorsReporter)
    }

    @Test
    fun `asynchronous replacement does not report cancellation as an error`() {
        view.setDataAsync(card, tag, onBindingPhase = {})
        worker.settle()

        view.setDataAsync(data(text(text = "Replacement")), DivDataTag("replacement"), onBindingPhase = {})
        worker.settle()

        verifyNoInteractions(errorsReporter)
    }

    @Test
    fun `dispatcher cancellation does not report an error`() {
        view.setDataAsync(card, tag, onBindingPhase = {})

        view.viewComponent.bindingDispatcher.cancelPendingTasks()
        worker.settle()

        verifyNoInteractions(errorsReporter)
    }

    @Test
    fun `successful preparation and attachment do not report an error`() {
        view.setDataAsync(card, tag, onBindingPhase = {})
        worker.settle()

        attachView()
        worker.settle()

        verifyNoInteractions(errorsReporter)
    }

    @Test
    fun `a throwing errors reporter does not prevent synthetic failure delivery`() {
        doThrow(IllegalStateException("Reporter failed")).whenever(errorsReporter)
            .onRuntimeError(anyOrNull(), any(), any())
        val phases = mutableListOf<DivBindingPhase>()

        view.setDataAsync(null, tag, onBindingPhase = { phases += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED), phases)
    }

    @Test
    fun `deferred content binds when phase callback and errors reporter throw`() {
        doThrow(IllegalStateException("Reporter failed")).whenever(errorsReporter)
            .onRuntimeError(anyOrNull(), any(), any())
        val content = data(text(id = "content", text = "Ready"))

        view.setDataAsync(content, tag, onBindingPhase = { error("Phase callback failed") })
        worker.settle()
        attachView()
        worker.settle()

        assertEquals("Ready", view.findViewWithTag<TextView>("content")?.text?.toString())
    }

    private fun reportedFailure(): String {
        verify(errorsReporter, only()).onRuntimeError(anyOrNull(), any(), errors.capture())
        return errors.firstValue.toString().replace(Regex("request=[0-9a-f]+"), "request=<identity>")
    }

    private fun attachView() = activity.value.apply {
        get().setContentView(view)
        visible()
    }

    private fun createView(): Div2View {
        val context = Div2Context(testContextThemeWrapper(),
            DivConfiguration.Builder(mock())
                .enableBindOnAttach(true)
                .extension(extension)
                .divErrorsReporter(errorsReporter)
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
