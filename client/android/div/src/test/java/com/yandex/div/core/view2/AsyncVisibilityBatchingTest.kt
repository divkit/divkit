package com.yandex.div.core.view2

import android.app.Activity
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivData
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.json.JSONObject
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowSystemClock

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class AsyncVisibilityBatchingTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val worker = ControlledBindingExecutor()
    private val actions = mutableListOf<String>()
    private lateinit var view: Div2View
    private lateinit var tracker: DivVisibilityActionTracker
    private var activity: ActivityController<Activity>? = null

    @BeforeTest
    fun setUp() = onMain {
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
        tracker = spy(DivVisibilityActionTracker(ViewVisibilityCalculator()))
        val component = TestComponent(
            backing.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                backing.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), worker.executor) },
            ),
            visibilityActionTracker = tracker,
        )
        val context = spy(backing) { on { div2Component } doReturn component }
        view = Div2View(context)
    }

    @AfterTest
    fun tearDown() {
        try {
            worker.close()
        } finally {
            onMain { activity.use { view.cleanup() } }
        }
    }

    @Test
    fun `async gallery preparation tracks every child once`() {
        val children = List(6) {
            text(
                id = "child-$it", text = constant("Tracked item"), width = fixed(100), height = fixed(40),
                visibilityActions = listOf(DivVisibilityAction(
                    logId = constant("child-$it"), logLimit = constant(0L), visibilityDuration = constant(100L),
                )),
            )
        }
        val document = data(gallery(
            width = fixed(300), height = fixed(200),
            items = children.chunked(2).map {
                container(width = fixed(100), height = fixed(80), items = it)
            },
        ))

        onMain { view.setDataAsync(document, DivDataTag("async-gallery-visibility"), null) }
        worker.settle()

        children.forEach { child ->
            verify(tracker).trackVisibilityActionsOf(eq(view), any(), isNull(), eq(child), any(), any())
        }
    }

    @Test
    fun `async gallery dispatches each visible action once despite repeated visibility checks`() {
        showAsyncGallery()
        advanceTime(1000)

        onMain { view.tryLogVisibility() }
        advanceTime(1000)

        onMain { assertEquals(listOf("gallery-first", "gallery-second"), actions.sorted()) }
    }

    @Test
    fun `detaching an async gallery cancels its pending appearance actions`() {
        showAsyncGallery()
        advanceTime(50)

        onMain { (view.parent as ViewGroup).removeView(view) }
        advanceTime(1000)

        onMain { assertEquals(emptyList(), actions) }
    }

    private fun showAsyncGallery() {
        val document = galleryCard()
        var completed = false
        onMain {
            activity = Robolectric.buildActivity(Activity::class.java).setup().also {
                it.get().window.setLayout(400, 400)
                it.get().setContentView(view)
                it.visible()
            }
            view.setDataAsync(document, DivDataTag("gallery-actions")) { completed = true }
        }
        worker.settle()
        onMain {
            check(completed && view.divData === document) { "Async binding must finish before checking visibility" }
            val root = checkNotNull(activity).get().window.decorView
            root.measure(makeMeasureSpec(400, EXACTLY), makeMeasureSpec(400, EXACTLY))
            root.layout(0, 0, 400, 400)
            view.tryLogVisibility()
            check(actions.isEmpty()) { "Gallery actions must wait for their visibility duration" }
        }
        worker.settle()
    }

    private fun advanceTime(milliseconds: Long) {
        worker.settle()
        ShadowSystemClock.advanceBy(milliseconds, TimeUnit.MILLISECONDS)
        ShadowLooper.idleMainLooper()
        worker.settle()
    }

    private fun galleryCard(): DivData = DivData(
        DivParsingEnvironment(logger = { throw AssertionError("Invalid gallery JSON", it) }),
        JSONObject("""
            {
                "log_id": "gallery",
                "states": [{
                    "state_id": 0,
                    "div": {
                        "type": "gallery",
                        "width": {"type": "fixed", "value": 300, "unit": "px"},
                        "height": {"type": "fixed", "value": 80, "unit": "px"},
                        "items": [
                            {
                                "type": "text", "text": "First",
                                "width": {"type": "fixed", "value": 100, "unit": "px"},
                                "height": {"type": "fixed", "value": 40, "unit": "px"},
                                "visibility_actions": [{
                                    "log_id": "gallery-first", "log_limit": 0, "visibility_duration": 1000
                                }]
                            },
                            {
                                "type": "text", "text": "Second",
                                "width": {"type": "fixed", "value": 100, "unit": "px"},
                                "height": {"type": "fixed", "value": 40, "unit": "px"},
                                "visibility_actions": [{
                                    "log_id": "gallery-second", "log_limit": 0, "visibility_duration": 1000
                                }]
                            }
                        ]
                    }
                }]
            }
        """.trimIndent()),
    )

    private fun <T> onMain(block: () -> T): T {
        val task = FutureTask<T> { block() }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
        try {
            return task.get()
        } catch (error: ExecutionException) {
            throw error.cause ?: error
        }
    }
}
