package com.yandex.div.core.view2

import android.app.Activity
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
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
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.same
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewStrictBindingTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val worker = ControlledBindingExecutor()
    private val activity = lazy { Robolectric.buildActivity(Activity::class.java).setup() }
    private val tag = DivDataTag("strict-binding")
    private val card = data(text(id = "content", text = "Prepared"))
    private val retryCard = data(text(id = "retry", text = "Prepared",
        extensions = listOf(DivExtension(id = "failure")),
    ))
    private val extension = spy(FailingExtension())
    private val errorsReporter = mock<DivErrorsReporter>()
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
    fun `root binding waits for attachment after preparation`() {
        val events = mutableListOf<String?>()
        allowPublication(events)

        view.setDataAsync(retryCard, tag, onBindingPhase = { events += it.name })
        worker.settle()

        assertEquals(listOf<String?>("PREPARED"), events)
    }

    @Test
    fun `legacy completion retains deferred binding before attachment`() {
        val rendered = mutableListOf<String?>()

        view.setDataAsync(card, tag) {
            rendered += view.findViewWithTag<TextView>("content")?.text?.toString()
        }
        worker.settle()

        assertEquals(listOf<String?>(null), rendered)
    }

    @Test
    fun `binding reports applied on main after initial publication`() {
        attachView()
        val results = mutableListOf<Pair<DivBindingPhase, Looper?>>()

        view.setDataAsync(card, tag, onBindingPhase = { results += it to Looper.myLooper() })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED to Looper.getMainLooper()), results)
    }

    @Test
    fun `detached binding reports prepared on main`() {
        val phases = mutableListOf<Pair<DivBindingPhase, Looper?>>()

        view.setDataAsync(card, tag, onBindingPhase = { phases += it to Looper.myLooper() })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.PREPARED to Looper.getMainLooper()), phases)
    }

    @Test
    fun `attachment from the prepared callback publishes the bound content`() {
        val rendered = mutableListOf<Pair<DivBindingPhase, String?>>()

        view.setDataAsync(card, tag, onBindingPhase = {
            rendered += it to view.findViewWithTag<TextView>("content")?.text?.toString()
            if (it == DivBindingPhase.PREPARED) attachView()
        })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.PREPARED to null, DivBindingPhase.APPLIED to "Prepared"), rendered)
    }

    @Test
    fun `synchronous replacement from the prepared callback cancels the previous request`() {
        val phases = mutableListOf<DivBindingPhase>()
        val replacement = data(text(id = "replacement", text = "Replacement"))

        view.setDataAsync(card, tag, onBindingPhase = {
            phases += it
            if (it == DivBindingPhase.PREPARED) view.setData(replacement, tag)
        })
        worker.settle()
        attachView()
        worker.settle()

        assertEquals<Pair<List<DivBindingPhase>, String?>>(
            listOf(DivBindingPhase.PREPARED, DivBindingPhase.CANCELLED) to "Replacement",
            phases to view.findViewWithTag<TextView>("replacement")?.text?.toString(),
        )
    }

    @Test
    fun `replacement queued from the prepared callback keeps its own attachment lifecycle`() {
        val events = mutableListOf<String>()
        val replacement = data(text(id = "replacement", text = "Replacement"))

        view.setDataAsync(card, tag, onBindingPhase = { phase ->
            events += "old:$phase"
            if (phase == DivBindingPhase.PREPARED) {
                view.setDataAsync(replacement, tag, onBindingPhase = { events += "new:$it" })
            }
        })
        worker.settle()
        attachView()
        worker.settle()

        assertEquals<Pair<List<String>, String?>>(
            listOf("old:PREPARED", "old:CANCELLED", "new:PREPARED", "new:APPLIED") to "Replacement",
            events to view.findViewWithTag<TextView>("replacement")?.text?.toString(),
        )
    }

    @Test
    fun `cleanup while awaiting attachment reports one cancellation`() {
        val phases = mutableListOf<DivBindingPhase>()
        view.setDataAsync(card, tag, onBindingPhase = { phases += it })
        worker.settle()

        view.cleanup()
        view.cleanup()
        worker.settle()
        attachView()
        worker.settle()

        assertEquals(listOf(DivBindingPhase.PREPARED, DivBindingPhase.CANCELLED), phases)
    }

    @Test
    fun `another prepared request reuses the root and owns its attachment`() {
        val oldPhases = mutableListOf<DivBindingPhase>()
        val newPhases = mutableListOf<DivBindingPhase>()
        view.setDataAsync(card, tag, onBindingPhase = { oldPhases += it })
        worker.settle()
        val preparedRoot = requireNotNull(view.getChildAt(0))

        view.setDataAsync(card, tag, onBindingPhase = { newPhases += it })
        worker.settle()
        val reusedRoot = requireNotNull(view.getChildAt(0))
        attachView()
        worker.settle()

        assertEquals(
            Triple(
                listOf(DivBindingPhase.PREPARED, DivBindingPhase.CANCELLED),
                listOf(DivBindingPhase.PREPARED, DivBindingPhase.APPLIED),
                preparedRoot,
            ),
            Triple(oldPhases.toList(), newPhases.toList(), reusedRoot),
        )
    }

    @Test
    fun `synchronous binding retries after an attachment failure`() {
        val parent = FrameLayout(activity.value.get())
        activity.value.get().setContentView(parent)
        activity.value.visible()
        view.setData(retryCard, tag)
        runCatching { parent.addView(view) }
        doNothing().whenever(extension).bindView(eq(view), any(), any(), any())

        parent.removeView(view)
        parent.addView(view)

        verify(extension, times(2)).bindView(eq(view), any(), any(), any())
    }

    @Test
    fun `attachment binding failure reaches the configured errors reporter`() {
        val failure = extension.failure
        view.setDataAsync(retryCard, tag, onBindingPhase = {})
        worker.settle()

        runCatching { attachView() }
        worker.settle()

        verify(errorsReporter).onRuntimeError(eq(retryCard), eq(tag), same(failure))
    }

    @Test
    fun `a throwing prepared callback still allows binding on attachment`() {
        val phases = mutableListOf<DivBindingPhase>()

        view.setDataAsync(card, tag, onBindingPhase = {
            phases += it
            if (it == DivBindingPhase.PREPARED) throw IllegalStateException("Prepared callback failed")
        })
        worker.settle()
        attachView()
        worker.settle()

        assertEquals(listOf(DivBindingPhase.PREPARED, DivBindingPhase.APPLIED), phases)
    }

    @Test
    fun `prepared callback failure reaches the configured errors reporter`() {
        val failure = IllegalStateException("Prepared callback failed")

        view.setDataAsync(card, tag, onBindingPhase = {
            if (it == DivBindingPhase.PREPARED) throw failure
        })
        worker.settle()

        verify(errorsReporter).onRuntimeError(eq(card), eq(tag), same(failure))
    }

    @Test
    fun `legacy completion is not repeated when prepared content attaches`() {
        val results = mutableListOf<Boolean>()
        view.setDataAsync(card, tag, onComplete = { results += it })
        worker.settle()

        attachView()
        worker.settle()

        assertEquals(listOf(true), results)
    }

    @Test
    fun `legacy success is not repeated when binding fails on attachment`() {
        val results = mutableListOf<Boolean>()
        view.setDataAsync(retryCard, tag, onComplete = { results += it })
        worker.settle()

        runCatching { attachView() }
        worker.settle()

        assertEquals(listOf(true), results)
    }

    @Test
    fun `cleanup from the applied callback does not cancel a completed request`() {
        attachView()
        val phases = mutableListOf<DivBindingPhase>()

        view.setDataAsync(card, tag, onBindingPhase = {
            phases += it
            view.cleanup()
        })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED), phases)
    }

    @Test
    fun `null data reports failed`() {
        val results = mutableListOf<DivBindingPhase>()

        view.setDataAsync(null, tag, onBindingPhase = { results += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED), results)
    }

    @Test
    fun `initial null data retains legacy success`() {
        val results = mutableListOf<Boolean>()

        view.setDataAsync(null, tag, onComplete = { results += it })
        worker.settle()

        assertEquals(listOf(true), results)
    }

    @Test
    fun `data without a root state reports failed`() {
        val results = mutableListOf<DivBindingPhase>()
        val emptyCard = data(states = emptyList())

        view.setDataAsync(emptyCard, tag, onBindingPhase = { results += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED), results)
    }

    @Test
    fun `compatible rebind reports applied`() {
        attachView()
        view.setDataAsync(card, tag, onComplete = null)
        worker.settle()
        val updatedCard = data(text(id = "content", text = "Updated"))
        val results = mutableListOf<DivBindingPhase>()

        view.setDataAsync(updatedCard, tag, onBindingPhase = { results += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED), results)
    }

    @Test
    fun `compatible rebind retains legacy false`() {
        attachView()
        view.setDataAsync(card, tag, onComplete = null)
        worker.settle()
        val updatedCard = data(text(id = "content", text = "Updated"))
        val results = mutableListOf<Boolean>()

        view.setDataAsync(updatedCard, tag, onComplete = { results += it })
        worker.settle()

        assertEquals(listOf(false), results)
    }

    @Test
    fun `compatible async rebind after synchronous binding retains legacy false`() {
        attachView()
        view.setData(card, tag)
        val updatedCard = data(text(id = "content", text = "Updated"))
        val results = mutableListOf<Boolean>()

        view.setDataAsync(updatedCard, tag, onComplete = { results += it })
        worker.settle()

        assertEquals(listOf(false), results)
    }

    @Test
    fun `compatible async rebind after synchronous binding reports applied`() {
        attachView()
        view.setData(card, tag)
        val updatedCard = data(text(id = "content", text = "Updated"))
        val results = mutableListOf<DivBindingPhase>()

        view.setDataAsync(updatedCard, tag, onBindingPhase = { results += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED), results)
    }

    @Test
    fun `same model after synchronous binding reports applied inline on main`() {
        attachView()
        view.setData(card, tag)
        val results = mutableListOf<Pair<DivBindingPhase, Looper?>>()

        view.setDataAsync(card, tag, onBindingPhase = { results += it to Looper.myLooper() })
        val inlineResults = results.toList()
        worker.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED to Looper.getMainLooper()), inlineResults)
    }

    @Test
    fun `cleanup reports one cancellation without publishing queued content`() {
        val results = mutableListOf<DivBindingPhase>()
        view.setDataAsync(card, tag, onBindingPhase = { results += it })

        view.cleanup()
        view.cleanup()
        worker.settle()

        assertEquals(listOf(DivBindingPhase.CANCELLED), results)
    }

    @Test
    fun `cancelling a prepared request suppresses its queued main publication`() {
        val results = mutableListOf<DivBindingPhase>()
        view.setDataAsync(card, tag, onBindingPhase = { results += it })
        worker.runWorker()

        view.viewComponent.bindingDispatcher.cancelPendingTasks()
        worker.settle()

        assertEquals(listOf(DivBindingPhase.CANCELLED), results)
    }

    @Test
    fun `publication failure reports failed on main`() {
        attachView()
        val results = mutableListOf<Pair<DivBindingPhase, Looper?>>()

        view.setDataAsync(retryCard, tag, onBindingPhase = { results += it to Looper.myLooper() })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED to Looper.getMainLooper()), results)
    }

    @Test
    fun `publication failure retains legacy false`() {
        attachView()
        val results = mutableListOf<Boolean>()

        view.setDataAsync(retryCard, tag, onComplete = { results += it })
        worker.settle()

        assertEquals(listOf(false), results)
    }

    @Test
    fun `publication failure is reported through the configured errors reporter`() {
        attachView()
        val failure = extension.failure
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()

        verify(errorsReporter).onRuntimeError(eq(retryCard), eq(tag), same(failure))
    }

    @Test
    fun `cancellation is not reported as a runtime error`() {
        view.setDataAsync(card, tag, onComplete = null)

        view.cleanup()
        worker.settle()

        verify(errorsReporter, never()).onRuntimeError(any(), any(), any())
    }

    @Test
    fun `throwing result callback does not report failed after applied`() {
        attachView()
        val results = mutableListOf<DivBindingPhase>()

        view.setDataAsync(card, tag, onBindingPhase = {
            results += it
            throw IllegalStateException("Result callback failed")
        })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED), results)
    }

    @Test
    fun `result callback failure is reported through the configured errors reporter`() {
        attachView()
        val failure = IllegalStateException("Result callback failed")

        view.setDataAsync(card, tag, onBindingPhase = { throw failure })
        worker.settle()

        verify(errorsReporter).onRuntimeError(eq(card), eq(tag), same(failure))
    }

    @Test
    fun `legacy old data overload retains deferred binding before attachment`() {
        val rendered = mutableListOf<String?>()

        view.setDataAsync(card, oldDivData = null, tag = tag) {
            rendered += view.findViewWithTag<TextView>("content")?.text?.toString()
        }
        worker.settle()

        assertEquals(listOf<String?>(null), rendered)
    }

    @Test
    fun `old data overload binds its prepared root on attachment`() {
        val rendered = mutableListOf<Pair<DivBindingPhase, String?>>()

        view.setDataAsync(card, oldDivData = null, tag = tag, onBindingPhase = {
            rendered += it to view.findViewWithTag<TextView>("content")?.text?.toString()
        })
        worker.settle()
        attachView()
        worker.settle()

        assertEquals(listOf(DivBindingPhase.PREPARED to null, DivBindingPhase.APPLIED to "Prepared"), rendered)
    }

    @Test
    fun `same model is not rebound automatically after a binding failure`() {
        attachView()
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()

        view.setDataAsync(retryCard, tag, onBindingPhase = {})
        worker.settle()

        verify(extension).bindView(eq(view), any(), any(), any())
    }

    @Test
    fun `same model after a failed binding reports failed without cleanup`() {
        attachView()
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()
        doNothing().whenever(extension).bindView(eq(view), any(), any(), any())
        val phases = mutableListOf<DivBindingPhase>()

        view.setDataAsync(retryCard, tag, onBindingPhase = { phases += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED), phases)
    }

    @Test
    fun `queued same model after a failed binding retains legacy true`() {
        attachView()
        val results = mutableListOf<Boolean>()

        view.setDataAsync(retryCard, tag, onComplete = null)
        view.setDataAsync(retryCard, tag, onComplete = { results += it })
        worker.settle()

        assertEquals(listOf(true), results)
    }

    @Test
    fun `explicit cleanup permits binding the same model after a failure`() {
        attachView()
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()
        view.cleanup()
        val events = mutableListOf<String?>()
        allowPublication(events)

        view.setDataAsync(retryCard, tag, onBindingPhase = { events += it.name })
        worker.settle()

        assertEquals(listOf<String?>("bound", "APPLIED"), events)
    }

    @Test
    fun `attachment binding failure reports failed before propagating the original exception`() {
        val parent = FrameLayout(activity.value.get())
        activity.value.get().setContentView(parent)
        activity.value.visible()
        val failure = extension.failure
        val phases = mutableListOf<DivBindingPhase>()
        view.setDataAsync(retryCard, tag, onBindingPhase = { phases += it })
        worker.settle()

        val thrown = runCatching { parent.addView(view) }.exceptionOrNull()

        assertEquals<Pair<List<DivBindingPhase>, Throwable?>>(
            listOf(DivBindingPhase.PREPARED, DivBindingPhase.FAILED) to failure,
            phases.toList() to thrown,
        )
    }

    @Test
    fun `idle media failure reports failed before propagating the original exception`() {
        prepareMediaReload()
        val failure = IllegalStateException("Media reload failed")
        doThrow(failure).whenever(extension).loadMedia(eq(view), any(), any(), any())
        val phases = mutableListOf<DivBindingPhase>()

        val thrown = runCatching {
            view.setDataAsync(retryCard, tag, onBindingPhase = { phases += it })
        }.exceptionOrNull()

        assertEquals<Pair<List<DivBindingPhase>, Throwable?>>(
            listOf(DivBindingPhase.FAILED) to failure,
            phases.toList() to thrown,
        )
    }

    @Test
    fun `successful same model reuse does not bind its extension again`() {
        attachView()
        val events = mutableListOf<String?>()
        allowPublication(events)
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()

        view.setDataAsync(retryCard, tag, onBindingPhase = {
            events += view.findViewWithTag<TextView>("retry")?.text?.toString()
        })
        worker.settle()

        assertEquals(listOf<String?>("bound", "Prepared"), events)
    }

    @Test
    fun `media reload failure on model reuse reports failed on main`() {
        prepareMediaReload()
        val failure = IllegalStateException("Media reload failed")
        doThrow(failure).whenever(extension).loadMedia(eq(view), any(), any(), any())
        val results = mutableListOf<Pair<DivBindingPhase, Looper?>>()

        runCatching {
            view.setDataAsync(retryCard, tag, onBindingPhase = { results += it to Looper.myLooper() })
        }
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED to Looper.getMainLooper()), results)
    }

    @Test
    fun `media reload failure with phase observation reaches the configured errors reporter`() {
        val failure = IllegalStateException("Media reload failed")
        prepareMediaReload()
        doThrow(failure).whenever(extension).loadMedia(eq(view), any(), any(), any())

        runCatching {
            view.setDataAsync(retryCard, tag, onBindingPhase = {})
        }
        worker.settle()

        verify(errorsReporter).onRuntimeError(eq(retryCard), eq(tag), same(failure))
    }

    @Test
    fun `cleanup during media reload cancels the reused model request`() {
        prepareMediaReload()
        val results = mutableListOf<DivBindingPhase>()
        doAnswer {
            view.cleanup()
            Unit
        }.whenever(extension).loadMedia(eq(view), any(), any(), any())

        view.setDataAsync(retryCard, tag, onBindingPhase = { results += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.CANCELLED), results)
    }

    @Test
    fun `successful model remains reusable after a null request`() {
        attachView()
        val events = mutableListOf<String?>()
        allowPublication(events)
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()
        view.setDataAsync(null, tag, onBindingPhase = { events += "null:$it" })
        worker.settle()

        view.setDataAsync(retryCard, tag, onBindingPhase = {
            events += view.findViewWithTag<TextView>("retry")?.text?.toString()
        })
        worker.settle()

        assertEquals(listOf<String?>("bound", "null:FAILED", "Prepared"), events)
    }

    @Test
    fun `cancelled prepared model cannot publish over its replacement`() {
        attachView()
        val events = mutableListOf<String?>()
        view.setDataAsync(retryCard, tag, onBindingPhase = { events += "cancelled:$it" })
        worker.runWorker()
        view.viewComponent.bindingDispatcher.cancelPendingTasks()
        allowPublication(events)
        val replacement = data(text(id = "replacement", text = "Replacement",
            extensions = listOf(DivExtension(id = "failure")),
        ))

        view.setDataAsync(replacement, tag, onBindingPhase = {
            events += view.findViewWithTag<TextView>("replacement")?.text?.toString()
        })
        worker.settle()

        assertEquals(listOf<String?>("cancelled:CANCELLED", "bound", "Replacement"), events)
    }

    private fun attachView() = activity.value.apply {
        get().setContentView(view)
        visible()
    }

    private fun prepareMediaReload() {
        attachView()
        doNothing().whenever(extension).bindView(eq(view), any(), any(), any())
        view.setDataAsync(retryCard, tag, onComplete = null)
        worker.settle()
        view.releaseMedia()
    }

    private fun allowPublication(events: MutableList<String?>) {
        doAnswer {
            events += "bound"
            Unit
        }.whenever(extension).bindView(eq(view), any(), any(), any())
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

    private class FailingExtension : DivExtensionHandler {
        val failure = IllegalStateException("Publication failed")

        override fun matches(div: DivBase): Boolean = div.extensions?.any { it.id == "failure" } == true

        override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
            throw failure
        }

        override fun unbindView(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) = Unit
    }
}
