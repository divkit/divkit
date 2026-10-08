package com.yandex.div.core.tooltip

import android.content.res.Resources
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import android.view.animation.AlphaAnimation
import android.view.animation.Transformation
import android.widget.PopupWindow
import com.yandex.div.core.asExpression
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class DivTooltipViewTest {

    private val context = testContextThemeWrapper()
    private val displayMetrics = DisplayMetrics().apply {
        density = 1f
        widthPixels = WINDOW_WIDTH
        heightPixels = WINDOW_HEIGHT
    }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn displayMetrics
    }
    private val anchor = mock<View> {
        on { width } doReturn ANCHOR_WIDTH
        on { height } doReturn ANCHOR_HEIGHT
        on { resources } doReturn resources
        on { getLocationInWindow(any()) } doAnswer { invocation ->
            val location = invocation.getArgument<IntArray>(0)
            location[0] = ANCHOR_X
            location[1] = ANCHOR_Y
        }
    }
    private val expressionResolver = mock<ExpressionResolverImpl>()
    private val runtimeStore = mock<RuntimeStore> {
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(expressionResolver)
    }
    private val divView = mock<Div2View> {
        on { runtimeStore } doReturn runtimeStore
        on { resources } doReturn resources
        on { getContext() } doReturn context
        on { getWindowVisibleDisplayFrame(any()) } doAnswer { invocation ->
            invocation.getArgument<Rect>(0).set(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT)
        }
    }
    private val tooltipDiv = fixedDiv()
    private val anchorBlock = DivBlock.Text(
        tooltipDiv,
        ExpressionResolver.EMPTY,
        DivStatePath.fromState(0),
    )
    private val tooltipView = View(context).apply {
        layoutParams = ViewGroup.LayoutParams(TOOLTIP_WIDTH, TOOLTIP_HEIGHT)
        tag = NESTED_VIEW_TAG
    }
    private val substrateView = View(context)
    private val container = DivTooltipContainer(context).apply {
        setViews(
            substrate = null,
            bringToTop = null,
            tooltip = this@DivTooltipViewTest.tooltipView,
        )
    }
    private val dismissListener = argumentCaptor<PopupWindow.OnDismissListener>()
    private val popupWindow = mock<SafePopupWindow> {
        on { contentView } doReturn container
        on { setOnDismissListener(dismissListener.capture()) } doAnswer { }
        on { dismiss() } doAnswer {
            dismissListener.lastValue.onDismiss()
        }
    }
    private val visibilityActionTracker = mock<DivVisibilityActionTracker>()
    private val animationsEnabledController = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doReturn false
    }

    @Test
    fun `show displays tooltip at anchor and reports completed layout`() {
        val onShown = mock<() -> Unit>()
        val underTest = createUnderTest()
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.show(onShown)
        layoutContainer(width = TOOLTIP_WIDTH, height = TOOLTIP_HEIGHT)

        val expectedX = ANCHOR_X + ANCHOR_WIDTH
        verify(popupWindow).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(popupWindow).update(expectedX, ANCHOR_Y, TOOLTIP_WIDTH, TOOLTIP_HEIGHT)
        verify(onShown).invoke()
    }

    @Test
    fun `completed show starts visibility tracking`() {
        val underTest = createUnderTest()
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.show {}
        layoutContainer(width = TOOLTIP_WIDTH, height = TOOLTIP_HEIGHT)

        verify(visibilityActionTracker).trackVisibilityActionsOf(
            scope = eq(divView),
            resolver = eq(expressionResolver),
            view = eq(tooltipView),
            div = eq(tooltipDiv),
            appearActions = any(),
            disappearActions = any(),
        )
    }

    @Test
    fun `synchronous dismiss from shown callback leaves visibility tracking stopped`() {
        val underTest = createUnderTest()
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.show {
            underTest.dismiss()
        }
        layoutContainer(width = TOOLTIP_WIDTH, height = TOOLTIP_HEIGHT)

        inOrder(visibilityActionTracker).apply {
            verify(visibilityActionTracker).trackVisibilityActionsOf(
                scope = eq(divView),
                resolver = eq(expressionResolver),
                view = eq(tooltipView),
                div = eq(tooltipDiv),
                appearActions = any(),
                disappearActions = any(),
            )
            verify(visibilityActionTracker).trackVisibilityActionsOf(
                scope = divView,
                resolver = expressionResolver,
                view = null,
                div = tooltipDiv,
            )
            verify(visibilityActionTracker).getDivWithWaitingDisappearActions()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `dismiss closes popup and reports dismissed view`() {
        val onDismissed = mock<(DivTooltipView) -> Unit>()
        val underTest = createUnderTest(onDismissed = onDismissed)
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.dismiss()

        verify(popupWindow).clearAnimation()
        verify(popupWindow).dismiss()
        verify(visibilityActionTracker).trackVisibilityActionsOf(
            scope = divView,
            resolver = expressionResolver,
            view = null,
            div = tooltipDiv,
        )
        verify(onDismissed).invoke(underTest)
    }

    @Test
    fun `dismiss reports window change when accessibility is enabled`() {
        val events = mutableListOf<AccessibilityEvent>()
        container.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) {
                events.add(event)
            }
        }
        val accessibilityStateProvider = mock<AccessibilityStateProvider> {
            on { isAccessibilityEnabled(any()) } doReturn true
        }
        val underTest = createUnderTest(accessibilityStateProvider = accessibilityStateProvider)
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.dismiss()

        assertEquals(listOf(TYPE_WINDOW_STATE_CHANGED), events.map { it.eventType })
    }

    @Test
    fun `immediate dismiss clears view animations before closing popup`() {
        container.setViews(
            substrate = substrateView,
            bringToTop = null,
            tooltip = tooltipView,
        )
        substrateView.startAnimation(AlphaAnimation(0f, 1f))
        tooltipView.startAnimation(AlphaAnimation(0f, 1f))
        val underTest = createUnderTest(data = tooltipData(substrateDiv = fixedDiv()))
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.dismissImmediately()

        assertNull(substrateView.animation)
        assertNull(tooltipView.animation)
        verify(popupWindow).clearAnimation()
        verify(popupWindow).dismiss()
    }

    @Test
    fun `popup dismiss tracks pending tooltip disappear action`() {
        whenever(visibilityActionTracker.getDivWithWaitingDisappearActions())
            .doReturn(mapOf(tooltipView to tooltipDiv))
        createUnderTest()

        dismissListener.lastValue.onDismiss()

        verify(visibilityActionTracker).trackDetachedView(
            view = tooltipView,
            div = tooltipDiv,
            resolver = expressionResolver,
            divView = divView,
        )
    }

    @Test
    fun `recompute fills popup window and positions tooltip when substrate exists`() {
        container.setViews(
            substrate = substrateView,
            bringToTop = null,
            tooltip = tooltipView,
        )
        val underTest = createUnderTest(data = tooltipData(substrateDiv = fixedDiv()))
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.recomputePosition()
        layoutContainer(width = WINDOW_WIDTH, height = WINDOW_HEIGHT)

        verify(popupWindow).update(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT)
        assertEquals(
            Rect(
                ANCHOR_X + ANCHOR_WIDTH,
                ANCHOR_Y,
                ANCHOR_X + ANCHOR_WIDTH + TOOLTIP_WIDTH,
                ANCHOR_Y + TOOLTIP_HEIGHT,
            ),
            tooltipView.bounds,
        )
    }

    @Test
    fun `recompute respects measured width for match parent tooltip`() {
        tooltipView.layoutParams = DivLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TOOLTIP_HEIGHT).apply {
            maxWidth = MAX_TOOLTIP_WIDTH
        }
        val underTest = createUnderTest(data = tooltipData(tooltipDiv = matchParentDiv()))
        whenever(popupWindow.isShowing).doReturn(true)

        underTest.recomputePosition()

        val expectedX = ANCHOR_X + ANCHOR_WIDTH
        verify(popupWindow).update(expectedX, ANCHOR_Y, MAX_TOOLTIP_WIDTH, TOOLTIP_HEIGHT)
    }

    @Test
    fun `recompute leaves popup unchanged while tooltip is hidden`() {
        val underTest = createUnderTest()
        whenever(popupWindow.isShowing).doReturn(false)

        underTest.recomputePosition()

        verify(popupWindow, never()).update(any(), any(), any(), any())
    }

    @Test
    fun `find view with tag reads popup content`() {
        val underTest = createUnderTest()

        val foundView = underTest.findViewWithTag(NESTED_VIEW_TAG)

        assertSame(tooltipView, foundView)
    }

    @Test
    fun `show configures popup transitions when animations are enabled`() {
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        val underTest = createUnderTest()

        underTest.show {}

        verify(popupWindow).enterTransition = any()
        verify(popupWindow).exitTransition = any()
    }

    @Test
    fun `show with disabled animations leaves popup transitions unset`() {
        val underTest = createUnderTest()

        underTest.show {}

        verify(popupWindow, never()).enterTransition = any()
        verify(popupWindow, never()).exitTransition = any()
    }

    @Test
    fun `show with substrate animates both views`() {
        setSubstrate()
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        val underTest = createUnderTest(data = tooltipData(substrateDiv = fixedDiv()))

        underTest.show {}

        assertNotNull(tooltipView.animation)
        assertNotNull(substrateView.animation)
        verify(popupWindow, never()).enterTransition = any()
    }

    @Test
    fun `dismiss with substrate waits for exit animation`() {
        setSubstrate()
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        whenever(popupWindow.isShowing).doReturn(true)
        val underTest = createUnderTest(data = tooltipData(substrateDiv = fixedDiv()))

        underTest.dismiss()

        assertNotNull(tooltipView.animation)
        assertNotNull(substrateView.animation)
        verify(popupWindow, never()).dismiss()
    }

    @Test
    fun `completed substrate exit closes popup`() {
        // Arrange
        setSubstrate()
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        whenever(popupWindow.isShowing).doReturn(true)
        val underTest = createUnderTest(data = tooltipData(substrateDiv = fixedDiv()))
        underTest.dismiss()
        val animation = requireNotNull(tooltipView.animation)

        // Act
        animation.getTransformation(0, Transformation())
        animation.getTransformation(animation.duration + 1, Transformation())
        ShadowLooper.runUiThreadTasks()

        // Assert
        verify(popupWindow).dismiss()
    }

    @Test
    fun `disabling animations after show clears popup exit transition`() {
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        whenever(popupWindow.isShowing).doReturn(true)
        val underTest = createUnderTest()
        underTest.show {}
        whenever(animationsEnabledController.isEnabled()).doReturn(false)

        underTest.dismiss()

        verify(popupWindow).exitTransition = null
        verify(popupWindow).dismiss()
    }

    @Test
    fun `enabled popup exit transition is retained on dismiss`() {
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        whenever(popupWindow.isShowing).doReturn(true)
        val underTest = createUnderTest()
        underTest.show {}

        underTest.dismiss()

        verify(popupWindow, never()).exitTransition = null
        verify(popupWindow).dismiss()
    }

    @Test
    fun `disabling animations after substrate show dismisses immediately`() {
        // Arrange
        setSubstrate()
        whenever(animationsEnabledController.isEnabled()).doReturn(true)
        whenever(popupWindow.isShowing).doReturn(true)
        val underTest = createUnderTest(data = tooltipData(substrateDiv = fixedDiv()))
        underTest.show {}
        whenever(animationsEnabledController.isEnabled()).doReturn(false)

        // Act
        underTest.dismiss()

        // Assert
        assertNull(tooltipView.animation)
        assertNull(substrateView.animation)
        verify(popupWindow).dismiss()
    }

    private fun setSubstrate() {
        container.setViews(
            substrate = substrateView,
            bringToTop = null,
            tooltip = tooltipView,
        )
    }

    private fun createUnderTest(
        data: TooltipData = tooltipData(),
        onDismissed: (DivTooltipView) -> Unit = {},
        accessibilityStateProvider: AccessibilityStateProvider = AccessibilityStateProvider(false),
    ): DivTooltipView {
        return DivTooltipView(
            data = data,
            container = container,
            popupWindow = popupWindow,
            onBackPressedCallback = null,
            accessibilityStateProvider = accessibilityStateProvider,
            divVisibilityActionTracker = visibilityActionTracker,
            animationsEnabledController = animationsEnabledController,
            onDismissed = onDismissed,
        )
    }

    private fun layoutContainer(width: Int, height: Int) {
        container.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
        )
        container.layout(0, 0, width, height)
    }

    private fun tooltipData(
        tooltipDiv: Div = this.tooltipDiv,
        substrateDiv: Div? = null,
    ): TooltipData {
        val tooltip = DivTooltip(
            id = "tooltip",
            div = tooltipDiv,
            substrateDiv = substrateDiv,
            position = DivTooltip.Position.RIGHT.asExpression(),
        )
        return TooltipData(
            id = tooltip.id,
            scopeId = null,
            divTooltip = tooltip,
            anchor = anchor,
            anchorBlock = anchorBlock,
            divView = divView,
        )
    }

    private val View.bounds: Rect
        get() = Rect(left, top, right, bottom)

    private companion object {
        const val WINDOW_WIDTH = 1_000
        const val WINDOW_HEIGHT = 600
        const val ANCHOR_X = 300
        const val ANCHOR_Y = 200
        const val ANCHOR_WIDTH = 100
        const val ANCHOR_HEIGHT = 50
        const val TOOLTIP_WIDTH = 100
        const val TOOLTIP_HEIGHT = 50
        const val MAX_TOOLTIP_WIDTH = 400
        const val NESTED_VIEW_TAG = "nested"

        fun fixedDiv(): Div.Text {
            return Div.Text(
                DivText(
                    text = "tooltip".asExpression(),
                    width = DivSize.Fixed(
                        DivFixedSize(
                            value = TOOLTIP_WIDTH.toLong().asExpression(),
                            unit = DivSizeUnit.PX.asExpression(),
                        ),
                    ),
                    height = DivSize.Fixed(
                        DivFixedSize(
                            value = TOOLTIP_HEIGHT.toLong().asExpression(),
                            unit = DivSizeUnit.PX.asExpression(),
                        ),
                    ),
                ),
            )
        }

        fun matchParentDiv(): Div.Text {
            return Div.Text(
                DivText(
                    text = "tooltip".asExpression(),
                    width = DivSize.MatchParent(DivMatchParentSize()),
                    height = DivSize.Fixed(
                        DivFixedSize(
                            value = TOOLTIP_HEIGHT.toLong().asExpression(),
                            unit = DivSizeUnit.PX.asExpression(),
                        ),
                    ),
                ),
            )
        }
    }
}
