package com.yandex.div.core.tooltip

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import com.yandex.div.core.asExpression
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTooltipVisibilityControllerTest {

    private val displayMetrics = DisplayMetrics().apply { density = 1f }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn displayMetrics
    }

    private val div = Div.Text(DivText(text = "test1".asExpression()))
    private val expressionResolver = ExpressionResolver.EMPTY
    private val div2View = mock<Div2View>()
    private val bindingContext = BindingContext(div2View, expressionResolver)

    private val anchor = mock<View>()
    private val animationCaptor = argumentCaptor<Animation>()
    private val tooltipView = mock<View> {
        on { context } doReturn mock()
        on { resources } doReturn resources
        on { startAnimation(animationCaptor.capture()) } doAnswer { }
    }
    private val substrateView = mock<View> {
        on { resources } doReturn resources
        on { startAnimation(any()) } doAnswer { }
    }
    private val tooltipContainer = mock<DivTooltipContainer> {
        on { tooltipView } doReturn tooltipView
        on { context } doReturn mock()
        on { isLayoutRequested } doReturn false
        on { width } doReturn 100
        on { height } doReturn 50
    }

    private val popupWindow = mock<SafePopupWindow> {
        on { contentView } doReturn tooltipContainer
        on { isShowing } doReturn true
    }

    private val visibilityActionTracker = mock<DivVisibilityActionTracker>()
    private val animationsEnabledController = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doReturn true
    }

    private val underTest = DivTooltipVisibilityController(
        visibilityActionTracker,
        animationsEnabledController,
    )

    @Test
    fun `tooltip is shown`() {
        val onShown = mock<() -> Unit>()

        underTest.showTooltip(tooltipData(), onShown)

        verify(popupWindow).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(onShown).invoke()
    }

    @Test
    fun `visibility tracking is started on show`() {
        underTest.showTooltip(tooltipData()) {}

        verify(visibilityActionTracker).trackVisibilityActionsOf(
            scope = eq(div2View),
            resolver = eq(expressionResolver),
            view = any(),
            div = eq(div),
            appearActions = any(),
            disappearActions = any(),
        )
    }

    @Test
    fun `showTooltip setups popup animation when animations enabled and no substrate`() {
        underTest.showTooltip(tooltipData()) {}

        verify(popupWindow).enterTransition = any()
        verify(popupWindow).exitTransition = any()
    }

    @Test
    fun `showTooltip does not setup popup animation when animations disabled`() {
        whenever(animationsEnabledController.isEnabled()).doReturn(false)

        underTest.showTooltip(tooltipData()) {}

        verify(popupWindow, never()).enterTransition = any()
        verify(popupWindow, never()).exitTransition = any()
    }

    @Test
    fun `dismissTooltip dismisses showing popup`() {
        val data = tooltipData()

        underTest.dismissTooltip(data)

        Assert.assertTrue(data.dismissed)
        verify(popupWindow).dismiss()
    }

    @Test
    fun `dismissTooltip stops visibility tracking when popup is not showing`() {
        whenever(popupWindow.isShowing).doReturn(false)
        val data = tooltipData()

        underTest.dismissTooltip(data)

        Assert.assertTrue(data.dismissed)
        verify(popupWindow, never()).dismiss()
        verify(visibilityActionTracker).trackVisibilityActionsOf(div2View, expressionResolver, null, div)
    }

    @Test
    fun `visibility tracking is stopped on dismiss`() {
        val data = tooltipData()
        underTest.showTooltip(data) {}
        reset(visibilityActionTracker)

        underTest.onDismiss(data)

        verify(visibilityActionTracker).trackVisibilityActionsOf(div2View, expressionResolver, null, div)
    }

    @Test
    fun `onDismiss tracks detached view with waiting disappear actions`() {
        val data = tooltipData()
        whenever(visibilityActionTracker.getDivWithWaitingDisappearActions())
            .doReturn(mapOf(tooltipContainer to div))

        underTest.onDismiss(data)

        verify(visibilityActionTracker).trackDetachedView(
            tooltipContainer,
            div,
            expressionResolver,
            div2View,
        )
    }

    @Test
    fun `tooltip exit animation is cleared when animations disabled between show and hide`() {
        val data = tooltipData()
        underTest.showTooltip(data) {}
        whenever(animationsEnabledController.isEnabled()).doReturn(false)

        underTest.hideTooltip(data)

        verify(popupWindow).exitTransition = null
        verify(popupWindow).dismiss()
    }

    @Test
    fun `tooltip exit animation is kept when animations enabled on hide`() {
        val data = tooltipData()
        underTest.showTooltip(data) {}

        underTest.hideTooltip(data)

        verify(popupWindow, never()).exitTransition = null
        verify(popupWindow).dismiss()
    }

    @Test
    fun `hideTooltip with substrate dismisses immediately when animations disabled`() {
        whenever(tooltipContainer.substrateView).doReturn(substrateView)
        whenever(animationsEnabledController.isEnabled()).doReturn(false)
        val data = tooltipData()

        underTest.hideTooltip(data)

        verify(substrateView).clearAnimation()
        verify(tooltipView).clearAnimation()
        verify(popupWindow).dismiss()
        verify(tooltipView, never()).startAnimation(any())
    }

    @Test
    fun `hideTooltip with substrate runs exit animation when animations enabled`() {
        whenever(tooltipContainer.substrateView).doReturn(substrateView)
        val data = tooltipData()

        underTest.hideTooltip(data)

        verify(tooltipView).startAnimation(any())
        verify(substrateView).startAnimation(any())
        verify(popupWindow, never()).dismiss()

        animationCaptor.lastValue.getAnimationListener()!!.onAnimationEnd(animationCaptor.lastValue)

        verify(popupWindow).dismiss()
    }

    @Test
    fun `showTooltip with substrate runs enter animation when animations enabled`() {
        whenever(tooltipContainer.substrateView).doReturn(substrateView)

        underTest.showTooltip(tooltipData()) {}

        verify(tooltipView).startAnimation(any())
        verify(substrateView).startAnimation(any())
        verify(popupWindow, never()).enterTransition = any()
    }

    private fun tooltipData(): TooltipData {
        val id = "tooltip_id"
        val tooltip = DivTooltip(
            div = div,
            id = id,
            position = DivTooltip.Position.RIGHT.asExpression(),
        )
        val data = TooltipData(id, null, bindingContext, tooltip, anchor)
        data.popupWindow = popupWindow
        return data
    }

    private fun Animation.getAnimationListener(): Animation.AnimationListener? {
        val field = Animation::class.java.getDeclaredField("mListener")
        field.isAccessible = true
        return field.get(this) as? Animation.AnimationListener
    }
}
