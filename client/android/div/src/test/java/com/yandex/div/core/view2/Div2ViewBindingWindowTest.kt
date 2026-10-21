package com.yandex.div.core.view2

import android.net.Uri
import android.os.Looper
import android.view.View.MeasureSpec
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewConfig
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivSightAction
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewBindingWindowTest {

    private val binding = ControlledBindingExecutor()
    private val divView = run {
        val backing = Div2Context(testContextThemeWrapper(), DivConfiguration.Builder(mock()).build())
        val component = TestComponent(
            backing.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                backing.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), binding.executor) },
            ),
            visibilityActionTracker = DivVisibilityActionTracker(
                mock { on { calculateVisibilityPercentage(any()) } doReturn 100 }
            ),
        )
        Div2View(spy(backing) { on { div2Component } doReturn component })
    }

    @AfterTest
    fun closeBinding() {
        try {
            binding.settle()
            divView.cleanup()
        } finally {
            binding.close()
        }
    }

    @Test
    fun `switchToState called during background binding of the same card is applied after it`() {
        divView.setData(twoStates("Before"), TAG)
        divView.setDataAsync(twoStates("After"), TAG) {}

        divView.switchToState(SECOND_STATE, true)
        binding.settle()

        assertEquals(SECOND_STATE, divView.currentStateId)
    }

    @Test
    fun `setData called during background binding replaces the pending data`() {
        val replacement = data(text(text = "Replacement"))
        divView.setDataAsync(data(text(text = "Pending")), TAG) {}
        binding.runNext()

        divView.setData(replacement, TAG)
        binding.settle()

        assertSame(replacement, divView.divData)
    }

    @Test
    fun `setConfig called during background binding survives the following setData`() {
        val config = DivViewConfig { false }
        divView.setDataAsync(data(text(text = "Pending")), TAG) {}
        binding.runNext()

        divView.setConfig(config)
        divView.setData(data(text(text = "Replacement")), TAG)
        binding.settle()

        assertSame(config, divView.config)
    }

    @Test
    fun `setData called during background binding cancels completion of the pending data`() {
        val completions = mutableListOf<Boolean>()
        divView.setDataAsync(data(text(text = "Pending")), TAG) { completions += it }
        binding.runNext()

        divView.setData(data(text(text = "Replacement")), TAG)
        binding.settle()

        assertEquals(emptyList(), completions)
    }

    @Test
    fun `cleanup during background binding dispatches waiting disappear actions on the main thread`() {
        val dispatchThreads = mutableListOf<Thread>()
        divView.actionHandler = object : DivActionHandler() {
            override fun handleAction(action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
                dispatchThreads += Thread.currentThread()
                return true
            }
        }
        divView.setData(data(text(text = "Shown", disappearActions = listOf(disappearAction(id = "gone")))), TAG)
        val spec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY)
        divView.measure(spec, spec)
        divView.layout(0, 0, 100, 100)
        ShadowLooper.idleMainLooper()
        divView.setDataAsync(data(text(text = "Pending")), TAG) {}
        binding.runNext()

        divView.cleanup()
        ShadowLooper.idleMainLooper()
        val worker = thread { binding.runWorker() }
        while (worker.isAlive) ShadowLooper.idleMainLooper()
        binding.settle()

        assertEquals(listOf(Looper.getMainLooper().thread), dispatchThreads)
    }

    @Test
    fun `cleanup requested by prepareForRecycleOrCleanup survives the following setData`() {
        val dispatchedLogIds = mutableListOf<String>()
        divView.actionHandler = object : DivActionHandler() {
            override fun handleAction(action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
                dispatchedLogIds += action.logId?.evaluate(resolver).orEmpty()
                return true
            }
        }
        divView.setData(data(text(text = "Shown", disappearActions = listOf(disappearAction(id = "gone")))), TAG)
        val spec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY)
        divView.measure(spec, spec)
        divView.layout(0, 0, 100, 100)
        ShadowLooper.idleMainLooper()
        divView.setDataAsync(data(text(text = "Pending")), TAG) {}
        binding.runNext()

        val replacement = data(text(text = "Replacement"))
        divView.prepareForRecycleOrCleanup(replacement)
        divView.setData(replacement, OTHER_TAG)
        ShadowLooper.idleMainLooper()
        val worker = thread { binding.runWorker() }
        while (worker.isAlive) ShadowLooper.idleMainLooper()
        binding.settle()

        assertEquals(listOf("gone"), dispatchedLogIds)
    }

    @Test
    fun `setVariable called after setDataAsync with another tag is applied to the new data`() {
        divView.setData(data(text(text = "Before"), variables = listOf(variable(VARIABLE, "before"))), TAG)
        divView.setDataAsync(data(text(text = "After"), variables = listOf(variable(VARIABLE, "initial"))), OTHER_TAG) {}

        divView.setVariable(VARIABLE, "updated")
        binding.settle()

        assertEquals("updated", divView.expressionResolver.getVariable(VARIABLE)?.getValue())
    }

    @Test
    fun `setData with the already bound data keeps calls deferred during background binding`() {
        val pending = data(text(text = "Pending"), variables = listOf(variable(VARIABLE, "initial")))
        divView.setDataAsync(pending, TAG) {}
        binding.runNext()
        divView.setVariable(VARIABLE, "updated")

        divView.setData(pending, TAG)
        binding.settle()

        assertEquals("updated", divView.expressionResolver.getVariable(VARIABLE)?.getValue())
    }

    @Test
    fun `deferred handleAction resolves expressions against the data bound by then`() {
        val handledUrls = mutableListOf<Uri?>()
        divView.actionHandler = object : DivActionHandler() {
            override fun handleAction(action: DivAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
                handledUrls += action.url?.evaluate(resolver)
                return true
            }
        }
        divView.setDataAsync(data(text(text = "Pending"), variables = listOf(variable(VARIABLE, TARGET_URL))), TAG) {}

        divView.handleAction(action(url = uriExpression("@{$VARIABLE}")))
        binding.settle()

        assertEquals(listOf(Uri.parse(TARGET_URL)), handledUrls)
    }

    private fun twoStates(text: String) = data(
        states = listOf(
            DivData.State(stateId = FIRST_STATE, div = text(text = text)),
            DivData.State(stateId = SECOND_STATE, div = text(text = text)),
        )
    )

    private companion object {
        const val FIRST_STATE = 0L
        const val SECOND_STATE = 1L
        const val VARIABLE = "target"
        const val TARGET_URL = "https://example.com/target"
        val TAG = DivDataTag("binding-window")
        val OTHER_TAG = DivDataTag("binding-window-other")
    }
}
