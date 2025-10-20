package com.yandex.div.core.tooltip

import android.app.Activity
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.asExpression
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivPoint
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivVisibilityAction
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class DivTooltipControllerTest {

    private val div2ViewWidth = 1000
    private val div2ViewHeight = 500

    private val displayMetrics = DisplayMetrics().apply {
        density = 1f
    }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn displayMetrics
        on { getIdentifier(any(), any(), any()) } doReturn 0
    }

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val action = DivVisibilityAction(logId = "visibility_action".asExpression())
    private val divBase = DivText(text = "test1".asExpression(), visibilityActions = listOf(action))
    private val div = Div.Text(divBase)

    private val tooltips = mutableListOf<DivTooltip>()

    private val anchor = mock<View> {
        on { getTag(R.id.div_tooltips_tag) } doReturn tooltips
        on { resources } doReturn resources
        on { width } doReturn 300
        on { height } doReturn 100
        on { isAttachedToWindow } doReturn true
        on {
            getLocationInWindow(any())
        }.doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = 100
            location[1] = 200
            null
        }
    }

    private val expressionResolver = mock<ExpressionResolver>()
    private val div2View = mock<Div2View> {
        on { resources } doReturn resources
        on { getWindowVisibleDisplayFrame(any()) } doAnswer { inv ->
            (inv.arguments[0] as Rect).set(0, 0, div2ViewWidth, div2ViewHeight)
        }
        on { getChildAt(0) } doReturn anchor
        on { childCount } doReturn 1
        on { getContext() } doReturn activity
    }
    private val bindingContext = BindingContext(div2View, expressionResolver)

    private val tooltipView = mock<View> {
        on { width } doReturn 100
        on { height } doReturn 50
        on { context } doReturn mock()
        on { resources } doReturn resources
    }

    private val tooltipWrapper = mock<DivTooltipContainer> {
        on { tooltipView } doReturn tooltipView
        on { isLayoutRequested } doReturn false
        on { width } doReturn 500
        on { height } doReturn 500
    }

    private val divTooltipViewBuilder = mock<DivTooltipViewBuilder> {
        on { buildTooltipView(any(), any(), anyInt(), anyInt()) } doReturn tooltipWrapper
    }

    private val tooltipShownCallback = mock<DivTooltipRestrictor.DivTooltipShownCallback>()

    private val tooltipRestrictor = mock<DivTooltipRestrictor> {
        on { canShowTooltip(any(), any(), any(), any()) } doReturn true
        on { tooltipShownCallback } doReturn tooltipShownCallback
    }
    private val visibilityActionTracker = mock<DivVisibilityActionTracker>()

    private val errorCollector = mock<ErrorCollector>()
    private val errorCollectors = mock<ErrorCollectors> {
        on { getOrCreate(anyOrNull(), anyOrNull()) } doReturn errorCollector
    }
    private val accessibilityStateProvider = AccessibilityStateProvider(false)

    private val divPreloader = mock<DivPreloader> {
        on { preload(any(), any(), any()) } doAnswer {
            (it.arguments[2] as DivPreloader.Callback).finish(false)
            object: DivPreloader.Ticket {
                override fun cancel() {}
            }
        }
    }

    private val dismissListener = argumentCaptor<PopupWindow.OnDismissListener>()
    private val popupWindow = mock<SafePopupWindow> {
        on { setOnDismissListener(dismissListener.capture()) } doAnswer { null }

        on { dismiss() } doAnswer {
            dismissListener.firstValue.onDismiss()
            null
        }
    }

    private val underTest = DivTooltipController(
        tooltipRestrictor, visibilityActionTracker, divPreloader, errorCollectors, divTooltipViewBuilder, accessibilityStateProvider
    ) { _, _, _ ->
        popupWindow
    }

    @Test
    fun `tooltip is shown`() {
        prepareDiv()

        underTest.showTooltip("tooltip_id", bindingContext)

        verify(popupWindow).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(tooltipShownCallback).onDivTooltipShown(div2View, anchor, tooltips[0])
    }

    @Test
    fun `visibility tracking is started on show`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        verify(visibilityActionTracker).trackVisibilityActionsOf(
            scope = eq(div2View),
            resolver = eq(expressionResolver),
            view = any(),
            div = eq(div),
            appearActions = any(),
            disappearActions = any()
        )
    }

    @Test
    fun `tooltip fit in container bounds if bigger then container`() {
        whenever(tooltipView.width).doReturn(div2ViewWidth.inc())
        whenever(tooltipView.height).doReturn(div2ViewHeight.inc())
        prepareDiv()

        underTest.showTooltip("tooltip_id", bindingContext)

        verify(tooltipShownCallback, times(1)).onDivTooltipShown(div2View, anchor, tooltips[0])
        verify(errorCollector, times(2)).logWarning(any())
    }

    @Test
    fun `tooltip is dismissed after timeout`() {
        prepareDiv(duration = 1000)
        underTest.showTooltip("tooltip_id", bindingContext)
        verify(tooltipShownCallback, never()).onDivTooltipDismissed(div2View, anchor, tooltips[0])

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(tooltipShownCallback).onDivTooltipDismissed(div2View, anchor, tooltips[0])
    }

    @Test
    fun `tooltip is dismissed on hideTooltip`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        verify(tooltipShownCallback, never()).onDivTooltipDismissed(div2View, anchor, tooltips[0])

        underTest.hideTooltip("tooltip_id", div2View)

        verify(tooltipShownCallback).onDivTooltipDismissed(div2View, anchor, tooltips[0])
    }

    @Test
    fun `visibility tracking is stopped on dismiss`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        reset(visibilityActionTracker)

        underTest.hideTooltip("tooltip_id", div2View)

        verify(visibilityActionTracker).trackVisibilityActionsOf(div2View, expressionResolver, null, div)
    }

    @Test
    fun `tooltip is dismissed on cleanup`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        verify(tooltipShownCallback, never()).onDivTooltipDismissed(div2View, anchor, tooltips[0])

        underTest.clear()

        verify(tooltipShownCallback).onDivTooltipDismissed(div2View, anchor, tooltips[0])
    }

    @Test
    fun `dismiss is canceled on cleanup`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        underTest.clear()
        reset(tooltipRestrictor.tooltipShownCallback)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verifyNoMoreInteractions(tooltipShownCallback)
    }

    @Test
    fun locationTest() {
        assertEquals(0 to 225, location(DivTooltip.Position.LEFT))
        assertEquals(0 to 150, location(DivTooltip.Position.TOP_LEFT))
        assertEquals(200 to 150, location(DivTooltip.Position.TOP))
        assertEquals(400 to 150, location(DivTooltip.Position.TOP_RIGHT))
        assertEquals(400 to 225, location(DivTooltip.Position.RIGHT))
        assertEquals(400 to 300, location(DivTooltip.Position.BOTTOM_RIGHT))
        assertEquals(200 to 300, location(DivTooltip.Position.BOTTOM))
        assertEquals(0 to 300, location(DivTooltip.Position.BOTTOM_LEFT))
    }

    @Test
    fun `tooltip not present at shown tooltips before restriction-check`() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any())).doAnswer {
            return@doAnswer underTest.captureCurrentTooltips().isEmpty()
        }
        prepareDiv()

        underTest.showTooltip("tooltip_id", bindingContext)

        verify(popupWindow).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(tooltipShownCallback).onDivTooltipShown(div2View, anchor, tooltips[0])
    }

    @Test
    fun `tooltip show restriction works`() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any())).doReturn(false)
        prepareDiv()

        underTest.showTooltip("tooltip_id", bindingContext)

        verify(popupWindow, never()).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(tooltipShownCallback, never()).onDivTooltipShown(div2View, anchor, tooltips[0])
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    private fun prepareDiv(duration: Long = 5000, offset: DivPoint? = null) {
        tooltips.add(
            DivTooltip(
                div = div,
                duration = duration.asExpression(),
                id = "tooltip_id",
                position = DivTooltip.Position.RIGHT.asExpression(),
                offset = offset
            )
        )
    }

    private fun location(position: DivTooltip.Position) =
        calcPopupLocation(
            tooltipView,
            anchor,
            DivTooltip(
                div = div,
                id = "id",
                offset = null,
                position = position.asExpression()
            ),
            ExpressionResolver.EMPTY,
        )

    private fun assertEquals(expected: Pair<Int, Int>, actual: Point) {
        Assert.assertEquals(expected.first, actual.x)
        Assert.assertEquals(expected.second, actual.y)
    }
}
