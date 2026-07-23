package com.yandex.div.core.tooltip

import android.app.Activity
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
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
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivPoint
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivVisibilityAction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
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

/**
 * Tests for [DivTooltipController].
 */
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

    private val anchorLayoutListeners = mutableListOf<View.OnLayoutChangeListener>()
    private val anchorPreDrawListeners = mutableListOf<ViewTreeObserver.OnPreDrawListener>()
    private val tooltipContainerLayoutListeners = mutableListOf<View.OnLayoutChangeListener>()

    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { isAlive } doReturn true
        on { addOnPreDrawListener(any()) } doAnswer { inv ->
            anchorPreDrawListeners.add(inv.arguments[0] as ViewTreeObserver.OnPreDrawListener)
            null
        }
        on { removeOnPreDrawListener(any()) } doAnswer { inv ->
            anchorPreDrawListeners.remove(inv.arguments[0] as ViewTreeObserver.OnPreDrawListener)
            null
        }
    }

    private val anchor = mock<View> {
        on { getTag(R.id.div_tooltips_tag) } doReturn tooltips
        on { resources } doReturn resources
        on { width } doReturn 300
        on { height } doReturn 100
        on { isAttachedToWindow } doReturn true
        on { viewTreeObserver } doReturn viewTreeObserver
        on {
            getLocationInWindow(any())
        }.doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = 100
            location[1] = 200
            null
        }
        on { addOnLayoutChangeListener(any()) } doAnswer { inv ->
            anchorLayoutListeners.add(inv.arguments[0] as View.OnLayoutChangeListener)
            null
        }
        on { removeOnLayoutChangeListener(any()) } doAnswer { inv ->
            anchorLayoutListeners.remove(inv.arguments[0] as View.OnLayoutChangeListener)
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
        on { context } doReturn activity
        on { isLayoutRequested } doReturn false
        on { width } doReturn 500
        on { height } doReturn 500
        on { addOnLayoutChangeListener(any()) } doAnswer { inv ->
            tooltipContainerLayoutListeners.add(inv.arguments[0] as View.OnLayoutChangeListener)
            null
        }
        on { removeOnLayoutChangeListener(any()) } doAnswer { inv ->
            tooltipContainerLayoutListeners.remove(inv.arguments[0] as View.OnLayoutChangeListener)
            null
        }
    }

    private val divTooltipViewBuilder = mock<DivTooltipViewBuilder> {
        on { buildTooltipView(any(), any(), anyOrNull(), any(), any()) } doReturn tooltipWrapper
    }

    private val tooltipShownCallback = mock<DivTooltipRestrictor.DivTooltipShownCallback>()

    private val tooltipRestrictor = mock<DivTooltipRestrictor> {
        on { canShowTooltip(any(), any(), any(), any(), anyOrNull()) } doReturn true
        on { tooltipShownCallback } doReturn tooltipShownCallback
    }
    private val visibilityActionTracker = mock<DivVisibilityActionTracker>()

    private val errorCollector = mock<ErrorCollector>()
    private val errorCollectors = mock<ErrorCollectors> {
        on { getOrCreate(anyOrNull(), anyOrNull()) } doReturn errorCollector
    }
    private val accessibilityStateProvider = AccessibilityStateProvider(false)

    private val divPreloader = mock<DivPreloader> {
        on { preload(any<Div>(), any(), any()) } doAnswer {
            (it.arguments[2] as DivPreloader.Callback).finish(false)
            object: DivPreloader.Ticket {
                override fun cancel() {}
            }
        }
    }

    private val dismissListener = argumentCaptor<PopupWindow.OnDismissListener>()
    private val popupWindow = mock<SafePopupWindow> {
        on { setOnDismissListener(dismissListener.capture()) } doAnswer { null }
        on { contentView } doReturn tooltipWrapper
        on { isShowing } doReturn true

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

        underTest.hideTooltip("tooltip_id")

        verify(tooltipShownCallback).onDivTooltipDismissed(div2View, anchor, tooltips[0])
    }

    @Test
    fun `visibility tracking is stopped on dismiss`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        reset(visibilityActionTracker)

        underTest.hideTooltip("tooltip_id")

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
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull())).doAnswer {
            return@doAnswer underTest.captureCurrentTooltips().isEmpty()
        }
        prepareDiv()

        underTest.showTooltip("tooltip_id", bindingContext)

        verify(popupWindow).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(tooltipShownCallback).onDivTooltipShown(div2View, anchor, tooltips[0])
    }

    @Test
    fun `tooltip show restriction works`() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull())).doReturn(false)
        prepareDiv()

        underTest.showTooltip("tooltip_id", bindingContext)

        verify(popupWindow, never()).showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
        verify(tooltipShownCallback, never()).onDivTooltipShown(div2View, anchor, tooltips[0])
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `when preload completes with failures tooltip can be shown again`() {
        val preloadCallback = argumentCaptor<DivPreloader.Callback>()
        val preloadTicket = mock<DivPreloader.Ticket>()
        whenever(divPreloader.preload(any<Div>(), any(), preloadCallback.capture()))
            .doReturn(preloadTicket)
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)

        preloadCallback.lastValue.finish(true)

        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(popupWindow, never()).showAtLocation(any(), any(), any(), any())
    }

    @Test
    fun `handleConfigurationChange starts anchor tracking and applies position`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        reset(popupWindow)
        whenever(popupWindow.isShowing).doReturn(true)
        whenever(popupWindow.contentView).doReturn(tooltipWrapper)

        underTest.handleConfigurationChange(div2View)

        verify(popupWindow, atLeastOnce()).update(any(), any(), any(), any())
        Assert.assertNotNull(underTest.captureCurrentTooltips().first().anchorTrackingDisposable)
        Assert.assertTrue(anchorLayoutListeners.isNotEmpty())
        Assert.assertTrue(anchorPreDrawListeners.isNotEmpty())
    }

    @Test
    fun `handleConfigurationChange reapplies position when anchor moves`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        underTest.handleConfigurationChange(div2View)
        reset(popupWindow)
        whenever(popupWindow.isShowing).doReturn(true)
        whenever(popupWindow.contentView).doReturn(tooltipWrapper)

        whenever(anchor.getLocationInWindow(any())).doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = 150
            location[1] = 250
            null
        }
        anchorLayoutListeners.toList().forEach { listener ->
            listener.onLayoutChange(anchor, 0, 0, 300, 100, 0, 0, 300, 100)
        }

        verify(popupWindow).update(any(), any(), any(), any())
    }

    @Test
    fun `handleConfigurationChange does not track hidden tooltip`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        whenever(popupWindow.isShowing).doReturn(false)

        underTest.handleConfigurationChange(div2View)

        Assert.assertNull(underTest.captureCurrentTooltips().first().anchorTrackingDisposable)
        Assert.assertTrue(anchorLayoutListeners.isEmpty())
        Assert.assertTrue(anchorPreDrawListeners.isEmpty())
    }

    @Test
    fun `handleConfigurationChange does not affect tooltips of other divViews`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)

        val otherDivView = mock<Div2View>()
        underTest.handleConfigurationChange(otherDivView)

        Assert.assertNull(underTest.captureCurrentTooltips().first().anchorTrackingDisposable)
        Assert.assertTrue(anchorLayoutListeners.isEmpty())
        Assert.assertTrue(anchorPreDrawListeners.isEmpty())
    }

    @Test
    fun `handleConfigurationChange replaces previous anchor tracking`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        underTest.handleConfigurationChange(div2View)
        val firstDisposable = underTest.captureCurrentTooltips().first().anchorTrackingDisposable
        Assert.assertNotNull(firstDisposable)

        underTest.handleConfigurationChange(div2View)

        val secondDisposable = underTest.captureCurrentTooltips().first().anchorTrackingDisposable
        Assert.assertNotNull(secondDisposable)
        Assert.assertNotSame(firstDisposable, secondDisposable)
        Assert.assertEquals(1, anchorLayoutListeners.size)
        Assert.assertEquals(1, anchorPreDrawListeners.size)
    }

    @Test
    fun `clear stops anchor tracking`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        underTest.handleConfigurationChange(div2View)
        Assert.assertTrue(anchorLayoutListeners.isNotEmpty())

        underTest.clear()

        Assert.assertTrue(anchorLayoutListeners.isEmpty())
        Assert.assertTrue(anchorPreDrawListeners.isEmpty())
    }

    @Test
    fun `hideTooltip stops anchor tracking`() {
        prepareDiv()
        underTest.showTooltip("tooltip_id", bindingContext)
        underTest.handleConfigurationChange(div2View)
        Assert.assertTrue(anchorLayoutListeners.isNotEmpty())

        underTest.hideTooltip("tooltip_id")

        Assert.assertTrue(anchorLayoutListeners.isEmpty())
        Assert.assertTrue(anchorPreDrawListeners.isEmpty())
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
