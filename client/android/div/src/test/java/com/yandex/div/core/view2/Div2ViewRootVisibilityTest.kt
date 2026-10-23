package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import android.view.View.MeasureSpec
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivDataChangeListener
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityAction
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivData
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivTransitionTrigger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewRootVisibilityTest {
    private val binding = ControlledBindingExecutor()
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val observedAnchorTexts = mutableListOf<String?>()
    private val views = mutableListOf<Div2View>()
    private val tag = DivDataTag("root-visibility")
    private val dataChangeListener = mock<DivDataChangeListener>()

    @AfterTest
    fun cleanup() {
        try {
            binding.settle()
            views.forEach(Div2View::cleanup)
            activity.pause().stop().destroy()
        } finally {
            binding.close()
        }
    }

    @Test
    fun `root visibility action finds the new anchor after data replacement`() {
        val view = attachInitialCard()

        view.setData(data(visibleText()), tag)
        layout(view)

        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    @Test
    fun `root visibility action finds the new anchor after asynchronous data replacement`() {
        val view = attachInitialCard()

        view.setDataAsync(data(visibleText()), tag) {}
        binding.settle()
        layout(view)

        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    @Test
    fun `root visibility action finds the anchor with bind on attach enabled`() {
        val view = attachInitialCard(bindOnAttach = true)

        view.setData(data(visibleText()), tag)
        layout(view)

        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    @Test
    fun `initial root visibility action waits for binding on attachment`() {
        val view = createView(bindOnAttach = true)
        view.setData(data(visibleText()), tag)

        activity.get().setContentView(view)
        layout(view)

        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    @Test
    fun `root visibility action finds the rebound anchor after switching state`() {
        val view = attachInitialCard(data(states = listOf(
            DivData.State(stateId = 0, div = text(id = "anchor", text = "Old anchor")),
            DivData.State(stateId = 1, div = visibleText()),
        )))

        view.switchToState(1, true)
        layout(view)

        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    @Test
    fun `root visibility action finds a new tree after switching state`() {
        val view = attachInitialCard(data(states = listOf(
            DivData.State(stateId = 0, div = container()),
            DivData.State(stateId = 1, div = visibleText()),
        )))

        view.switchToState(1, true)
        layout(view)

        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    @Test
    fun `root visibility action finds the anchor during animated data replacement`() {
        // Arrange
        val view = attachInitialCard()
        val replacementData = data(text(
            id = "anchor", text = "New anchor",
            visibilityActions = listOf(visibilityAction(id = "visible", delayMs = 0)),
            transitionIn = DivAppearanceTransition.Fade(
                DivFadeTransition(duration = constant(100L)),
            ),
            transitionTriggers = listOf(DivTransitionTrigger.DATA_CHANGE),
        ), transitionAnimationSelector = DivTransitionSelector.DATA_CHANGE)

        // Act
        view.setData(replacementData, tag)
        layout(view)

        // Assert
        verify(dataChangeListener).beforeAnimatedDataChange(view, replacementData)
        assertEquals(listOf<String?>("New anchor"), observedAnchorTexts)
    }

    private fun attachInitialCard(
        initialData: DivData = data(container(items = listOf(text(text = "Initial card")))),
        bindOnAttach: Boolean = false,
    ): Div2View {
        val view = createView(bindOnAttach)
        activity.get().setContentView(view)
        view.setData(initialData, tag)
        layout(view)
        return view
    }

    private fun createView(bindOnAttach: Boolean): Div2View {
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(bindOnAttach)
            .enableComplexRebind(false)
            .divDataChangeListener(dataChangeListener)
            .build()
        val backing = Div2Context(testContextThemeWrapper(), configuration)
        val component = TestComponent(
            backing.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                backing.div2Component.viewComponent(),
                bindingDispatcher = {
                    BindingDispatcher(it, BindingCriticalSection(), binding.executor)
                },
            ),
            visibilityActionTracker = DivVisibilityActionTracker(
                mock { on { calculateVisibilityPercentage(any()) } doReturn 100 },
            ),
        )
        val view = Div2View(spy(backing) { on { div2Component } doReturn component })
        view.actionHandler = object : DivActionHandler() {
            override fun handleAction(
                action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver,
            ): Boolean {
                val anchor = (view as Div2View).findViewWithTag<TextView>("anchor")
                observedAnchorTexts += anchor?.text?.toString()
                return true
            }
        }
        views += view
        return view
    }

    private fun visibleText() = text(
        id = "anchor", text = "New anchor",
        visibilityActions = listOf(visibilityAction(id = "visible", delayMs = 0)),
    )

    private fun layout(view: View) {
        val spec = MeasureSpec.makeMeasureSpec(300, MeasureSpec.EXACTLY)
        view.measure(spec, spec)
        view.layout(0, 0, 300, 300)
        ShadowLooper.idleMainLooper()
    }
}
