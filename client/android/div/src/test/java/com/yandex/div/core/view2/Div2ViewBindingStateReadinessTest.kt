package com.yandex.div.core.view2

import android.app.Activity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewBindingStateReadinessTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val worker = ControlledBindingExecutor()
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val tag = DivDataTag("binding-state-readiness")
    private val card = data(states = listOf(
        DivData.State(stateId = 0, div = text(id = "content", text = "First")),
        DivData.State(stateId = 1, div = text(id = "content", text = "Second")),
    ))
    private val extension = mock<DivExtensionHandler> {
        on { matches(any()) } doReturn true
    }
    private val view = run {
        val context = Div2Context(testContextThemeWrapper(),
            DivConfiguration.Builder(mock()).enableBindOnAttach(true).extension(extension).build(),
        )
        val component = TestComponent(context.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(context.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), worker.executor) },
            ),
        )
        Div2View(spy(context) { on { div2Component } doReturn component })
    }

    @BeforeTest
    fun attachView() {
        activity.get().setContentView(view)
        activity.visible()
    }

    @AfterTest
    fun cleanup() {
        try {
            view.cleanup()
            worker.close()
        } finally {
            activity.pause().stop().destroy()
        }
    }

    @Test
    fun `same model reports applied after switching to another root state`() {
        val initial = bindCard()

        view.switchToState(1)
        val switched = bindCard()

        assertEquals(
            listOf(DivBindingPhase.APPLIED to "First", DivBindingPhase.APPLIED to "Second"),
            initial + switched,
        )
    }

    @Test
    fun `same model remains applied after a nonexistent root state is requested`() {
        val initial = bindCard()

        view.switchToState(999)
        val unchanged = bindCard()

        assertEquals(
            listOf(DivBindingPhase.APPLIED to "First", DivBindingPhase.APPLIED to "First"),
            initial + unchanged,
        )
    }

    @Test
    fun `successful replacement stays applied when previous attachment binding fails`() {
        val parent = FrameLayout(activity.get())
        activity.get().setContentView(parent)
        val oldRoot = text(text = "Outdated", extensions = listOf(DivExtension(id = "replace")))
        val failure = IllegalStateException("Old attachment binding failed")
        doAnswer {
            view.setData(card, tag)
            throw failure
        }.whenever(extension).bindView(eq(view), any(), any(), eq(oldRoot.value()))
        bindCard(data(oldRoot))

        val thrown = runCatching { parent.addView(view) }.exceptionOrNull()
        val replacementText = view.findViewWithTag<TextView>("content")?.text?.toString()
        val reused = bindCard()

        assertEquals(
            Triple(failure, "First", listOf(DivBindingPhase.APPLIED to "First")),
            Triple(thrown, replacementText, reused),
        )
    }

    @Test
    fun `same model reports failed when its replacement root could not be installed`() {
        val oldRoot = text(text = "Outdated", extensions = listOf(DivExtension(id = "replace")))
        val replacement = data(container(items = listOf(text(text = "Replacement"))))
        view.setData(data(oldRoot), tag)
        doThrow(IllegalStateException("Old root release failed")).whenever(extension)
            .unbindView(eq(view), any(), any(), eq(oldRoot.value()))
        val phases = mutableListOf<DivBindingPhase>()

        view.setDataAsync(replacement, tag, onComplete = null)
        worker.settle()
        view.setDataAsync(replacement, tag, onBindingPhase = { phases += it })
        worker.settle()

        assertEquals(listOf(DivBindingPhase.FAILED), phases)
    }

    @Test
    fun `same prepared model reports failed after a nonexistent state cancels attachment binding`() {
        activity.get().setContentView(FrameLayout(activity.get()))
        bindCard()

        view.switchToState(999)
        val reused = bindCard()

        assertEquals(listOf(DivBindingPhase.FAILED), reused.map { it.first })
    }

    private fun bindCard(data: DivData = card): List<Pair<DivBindingPhase, String?>> {
        val results = mutableListOf<Pair<DivBindingPhase, String?>>()
        view.setDataAsync(data, tag, onBindingPhase = {
            results += it to view.findViewWithTag<TextView>("content")?.text?.toString()
        })
        worker.settle()
        return results.toList()
    }
}
