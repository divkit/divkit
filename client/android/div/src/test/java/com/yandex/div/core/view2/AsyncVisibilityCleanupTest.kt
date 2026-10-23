package com.yandex.div.core.view2

import android.app.Activity
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivData
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowChoreographer
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class AsyncVisibilityCleanupTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val worker = ControlledBindingExecutor()
    private val actions = mutableListOf<String>()
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val context = run {
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(false)
            .actionHandler(object : DivActionHandler() {
                override fun handleAction(action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
                    actions += checkNotNull(action.logId).evaluate(resolver)
                    return true
                }
            })
            .build()
        val backing = Div2Context(testContextThemeWrapper(), configuration)
        val component = TestComponent(
            backing.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                backing.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), worker.executor) },
            ),
        )
        spy(backing) { on { div2Component } doReturn component }
    }
    private val view = Div2View(context)
    private val tag = DivDataTag("async-visibility-cleanup")

    @BeforeTest
    fun attachView() {
        ShadowChoreographer.setPaused(true)
        activity.get().window.setLayout(400, 400)
        activity.get().setContentView(view)
        activity.visible()
    }

    @AfterTest
    fun tearDown() {
        try {
            worker.close()
        } finally {
            activity.use { view.cleanup() }
            ShadowLooper.idleMainLooper()
        }
    }

    @Test
    fun `async binding dispatches each visible action once`() {
        showAsyncCard(card("async"))

        advanceTime(100)

        assertEquals(listOf("async-first", "async-second"), actions.sorted())
    }

    @Test
    fun `synchronous binding dispatches each visible action once`() {
        view.setData(card("sync"), tag)
        layoutView()

        advanceTime(100)

        assertEquals(listOf("sync-first", "sync-second"), actions.sorted())
    }

    @Test
    fun `detaching before the deadline cancels pending appearance`() {
        showAsyncCard(card("detached"))
        advanceTime(50)

        (view.parent as ViewGroup).removeView(view)
        advanceTime(100)

        assertEquals(emptyList(), actions)
    }

    @Test
    fun `reattaching does not reuse the old appearance deadline`() {
        showAsyncCard(card("returned"))
        advanceTime(50)
        (view.parent as ViewGroup).removeView(view)
        advanceTime(100)

        activity.get().setContentView(view)
        layoutView()
        advanceTime(99)

        assertEquals(emptyList(), actions)
    }

    @Test
    fun `reattaching dispatches appearance after its full delay`() {
        showAsyncCard(card("returned"))
        advanceTime(50)
        (view.parent as ViewGroup).removeView(view)
        advanceTime(100)

        activity.get().setContentView(view)
        layoutView()
        advanceTime(100)

        assertEquals(listOf("returned-first", "returned-second"), actions.sorted())
    }

    @Test
    fun `async replacement within one tag dispatches only the new document actions`() {
        showAsyncCard(card("old"))
        advanceTime(50)

        showAsyncCard(card("new"))
        advanceTime(100)

        assertEquals(listOf("new-first", "new-second"), actions.sorted())
    }

    @Test
    fun `cleanup prevents queued async document actions`() {
        view.setDataAsync(card("cancelled"), tag, null)

        view.cleanup()
        runBindingFrame()
        layoutView()
        advanceTime(100)

        assertEquals(emptyList(), actions)
    }

    @Test
    fun `variable subscription observes preceding visibility publication`() {
        var visibilityCalculated = false
        val calculator = mock<ViewVisibilityCalculator> {
            on { calculateVisibilityPercentage(any()) } doAnswer {
                visibilityCalculated = true
                100
            }
        }
        val component = TestComponent(
            context.div2Component,
            visibilityActionTracker = DivVisibilityActionTracker(calculator),
        )
        doReturn(component).whenever(context).div2Component
        val observedView = Div2View(context)
        val variable = Variable.StringVariable("notification", "before")
        val controller = VariableControllerImpl(viewProvider = { observedView })
        val observations = mutableListOf<Boolean>()
        try {
            activity.get().setContentView(observedView)
            observedView.setData(card("tracked"), tag)
            observedView.measure(makeMeasureSpec(400, EXACTLY), makeMeasureSpec(400, EXACTLY))
            observedView.layout(0, 0, 400, 400)
            worker.settle()
            controller.declare(variable)
            visibilityCalculated = false

            observedView.viewComponent.bindingDispatcher.runOnBindingThread(BindingTaskType.DATA, onError = { throw it }) {
                observedView.trackChildrenVisibility()
                controller.subscribeToVariableChange(variable.name, invokeOnSubscription = true) {
                    observations += visibilityCalculated
                }
            }
            runBindingFrame()

            assertEquals(listOf(true), observations)
        } finally {
            observedView.cleanup()
        }
    }

    private fun showAsyncCard(data: DivData) {
        var completed = false
        view.setDataAsync(data, tag) { completed = true }
        runBindingFrame()
        check(completed && view.divData === data) { "Async binding must finish before checking visibility" }
        layoutView()
    }

    private fun runBindingFrame() {
        worker.settle()
        advanceTime(ShadowChoreographer.getFrameDelay().toMillis())
        worker.settle()
    }

    private fun layoutView() {
        val root = activity.get().window.decorView
        root.measure(makeMeasureSpec(400, EXACTLY), makeMeasureSpec(400, EXACTLY))
        root.layout(0, 0, 400, 400)
        view.tryLogVisibility()
        ShadowLooper.idleMainLooper()
    }

    private fun advanceTime(milliseconds: Long) {
        worker.settle()
        ShadowLooper.idleMainLooper(milliseconds, TimeUnit.MILLISECONDS)
        worker.settle()
    }

    private fun card(id: String): DivData = data(container(
        id = "root", width = fixed(200), height = fixed(80),
        items = listOf(trackedText("$id-first"), trackedText("$id-second")),
    ))

    private fun trackedText(id: String) = text(
        id = id, text = constant("Tracked item"), width = fixed(100), height = fixed(40),
        visibilityActions = listOf(DivVisibilityAction(
            logId = constant(id), logLimit = constant(0L), visibilityDuration = constant(100L),
        )),
    )
}
