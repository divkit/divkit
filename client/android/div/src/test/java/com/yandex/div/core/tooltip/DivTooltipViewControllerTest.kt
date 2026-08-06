package com.yandex.div.core.tooltip

import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import com.yandex.div.core.Disposable
import com.yandex.div.core.asExpression
import com.yandex.div.core.tooltip.DivTooltipViewController.Companion.calcPopupLocation
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltipMode
import com.yandex.div2.DivTooltipModeModal
import com.yandex.div2.DivTooltipModeNonModal
import org.junit.After
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
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTooltipViewControllerTest {

    private val div2ViewWidth = 1000
    private val div2ViewHeight = 500
    private val handler = Handler(Looper.getMainLooper())

    private val displayMetrics = DisplayMetrics().apply { density = 1f }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn displayMetrics
    }

    private val div = Div.Text(DivText(text = "test1".asExpression()))
    private val div2View = mock<Div2View> {
        on { resources } doReturn resources
        on { getContext() } doReturn mock()
        on { getWindowVisibleDisplayFrame(any()) } doAnswer { inv ->
            (inv.arguments[0] as Rect).set(0, 0, div2ViewWidth, div2ViewHeight)
        }
        on { childCount } doReturn 0
    }
    private val bindingContext = BindingContext(div2View, ExpressionResolver.EMPTY)

    private val layoutListenerCaptor = argumentCaptor<View.OnLayoutChangeListener>()
    private val preDrawListenerCaptor = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { isAlive } doReturn true
        on { addOnPreDrawListener(preDrawListenerCaptor.capture()) } doAnswer { }
    }

    private val anchor = mock<View> {
        on { resources } doReturn resources
        on { width } doReturn 300
        on { height } doReturn 100
        on { viewTreeObserver } doReturn viewTreeObserver
        on { getLocationInWindow(any()) } doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = 100
            location[1] = 200
            null
        }
        on { addOnLayoutChangeListener(layoutListenerCaptor.capture()) } doAnswer { }
    }

    private val tooltipView = mock<View> {
        on { width } doReturn 100
        on { height } doReturn 50
        on { resources } doReturn resources
        on { context } doReturn mock()
    }

    private val tooltipWrapper = mock<DivTooltipContainer> {
        on { tooltipView } doReturn tooltipView
        on { context } doReturn mock()
    }

    private val divTooltipViewBuilder = mock<DivTooltipViewBuilder> {
        on { buildTooltipView(any(), any(), anyOrNull(), any(), any()) } doReturn tooltipWrapper
    }

    private val errorCollector = mock<ErrorCollector>()
    private val errorCollectors = mock<ErrorCollectors> {
        on { getOrCreate(anyOrNull(), anyOrNull()) } doReturn errorCollector
    }
    private val accessibilityStateProvider = AccessibilityStateProvider(false)

    private val dismissListenerCaptor = argumentCaptor<PopupWindow.OnDismissListener>()
    private val popupWindow = mock<SafePopupWindow> {
        on { contentView } doReturn tooltipWrapper
        on { isShowing } doReturn true
        on { setOnDismissListener(dismissListenerCaptor.capture()) } doAnswer { }
    }

    private val underTest = DivTooltipViewController(
        errorCollectors,
        divTooltipViewBuilder,
        accessibilityStateProvider,
    ) { _, _, _ -> popupWindow }

    @After
    fun tearDown() {
        AccessibilityStateProvider.touchExplorationEnabled = null
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
    fun `onPopupShown logs size warnings when tooltip does not fit`() {
        whenever(tooltipView.width).doReturn(div2ViewWidth.inc())
        whenever(tooltipView.height).doReturn(div2ViewHeight.inc())

        underTest.onPopupShown(tooltipData())

        verify(errorCollector, times(2)).logWarning(any())
    }

    @Test
    fun `startAnchorPositionTracking does not log size warnings on relocate`() {
        whenever(tooltipView.width).doReturn(div2ViewWidth.inc())
        whenever(tooltipView.height).doReturn(div2ViewHeight.inc())
        startTracking()

        whenever(anchor.getLocationInWindow(any())).doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = 150
            location[1] = 250
            null
        }
        layoutListenerCaptor.lastValue.onLayoutChange(anchor, 0, 0, 300, 100, 0, 0, 300, 100)

        verify(errorCollector, never()).logWarning(any())
    }

    @Test
    fun `createPopupWindow configures popup and assigns it to data`() {
        Assert.assertSame(popupWindow, createPopup().popupWindow)
        verify(popupWindow).setTouchInterceptor(any())
        verify(popupWindow).setOnDismissListener(any())
        verify(popupWindow).isTouchable = true
        verify(popupWindow).isOutsideTouchable = true
        verify(popupWindow, never()).enterTransition = any()
    }

    @Test
    fun `createPopupWindow dismiss stops tracking and disables back press callback`() {
        val onDismiss = mock<() -> Unit>()
        val data = createPopup(onDismiss = onDismiss)
        val backCallback = mock<OnBackPressedCallback>()
        val tracking = mock<Disposable>()
        data.onBackPressedCallback = backCallback
        data.anchorTrackingDisposable = tracking

        dismissListenerCaptor.lastValue.onDismiss()

        verify(tracking).close()
        Assert.assertNull(data.anchorTrackingDisposable)
        verify(backCallback).isEnabled = false
        verify(onDismiss).invoke()
    }

    @Test
    fun `createPopupWindow assigns back press callback when accessibility enabled`() {
        AccessibilityStateProvider.touchExplorationEnabled = true
        val accessibilityEnabledController = DivTooltipViewController(
            errorCollectors,
            divTooltipViewBuilder,
            AccessibilityStateProvider(true),
        ) { _, _, _ -> popupWindow }
        val data = tooltipData()

        disableAssertions {
            accessibilityEnabledController.createPopupWindow(data, onTouchOutside = {}, onDismiss = {})
        }

        Assert.assertNotNull(data.onBackPressedCallback)
        Assert.assertTrue(data.onBackPressedCallback!!.isEnabled)
    }

    @Test
    fun `createPopupWindow with substrate attaches in decor`() {
        createPopup(tooltipData(substrateDiv = div))

        verify(popupWindow).isAttachedInDecor = true
        verify(popupWindow).isClippingEnabled = false
        verify(divTooltipViewBuilder).buildTooltipView(
            any(),
            any(),
            anyOrNull(),
            eq(ViewGroup.LayoutParams.MATCH_PARENT),
            eq(ViewGroup.LayoutParams.MATCH_PARENT),
        )
    }

    @Test
    fun `onPopupShown updates popup without substrate`() {
        underTest.onPopupShown(tooltipData())
        verify(popupWindow).update(400, 225, 100, 50)
    }

    @Test
    fun `onPopupShown positions tooltip inside substrate container`() {
        underTest.onPopupShown(tooltipData(substrateDiv = div))

        verify(popupWindow).update(0, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        verify(tooltipWrapper).setTooltipPosition(400, 225, 100, 50)
    }

    @Test
    fun `onPopupShown positions bring to top view`() {
        val bringToTopView = mock<View> {
            on { tag } doReturn "bring_to_top"
            on { width } doReturn 40
            on { height } doReturn 20
            on { getLocationOnScreen(any()) } doAnswer { inv ->
                val location = inv.arguments[0] as IntArray
                location[0] = 10
                location[1] = 20
                null
            }
        }
        whenever(div2View.childCount).doReturn(1)
        whenever(div2View.getChildAt(0)).doReturn(bringToTopView)
        whenever(tooltipWrapper.bringToTopView).doReturn(mock())

        underTest.onPopupShown(tooltipData(substrateDiv = div, bringToTopId = "bring_to_top"))

        verify(tooltipWrapper).setBringToTopPosition(10, 20)
    }

    @Test
    fun `startAnchorPositionTracking starts tracker and applies position`() {
        val data = startTracking()

        Assert.assertNotNull(data.anchorTrackingDisposable)
        verify(popupWindow, atLeastOnce()).update(any(), any(), any(), any())
    }

    @Test
    fun `startAnchorPositionTracking reapplies position when anchor moves`() {
        startTracking()
        reset(popupWindow)
        whenever(popupWindow.isShowing).doReturn(true)
        whenever(popupWindow.contentView).doReturn(tooltipWrapper)
        whenever(anchor.getLocationInWindow(any())).doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = 150
            location[1] = 250
            null
        }

        layoutListenerCaptor.lastValue.onLayoutChange(anchor, 0, 0, 300, 100, 0, 0, 300, 100)

        verify(popupWindow).update(any(), any(), any(), any())
    }

    @Test
    fun `startAnchorPositionTracking does nothing without popup`() {
        val data = tooltipData(withPopup = false)

        underTest.startAnchorPositionTracking(data, div2View, handler)

        Assert.assertNull(data.anchorTrackingDisposable)
        verify(anchor, never()).addOnLayoutChangeListener(any())
    }

    @Test
    fun `startAnchorPositionTracking does nothing for other divView`() {
        val data = tooltipData()

        underTest.startAnchorPositionTracking(data, mock(), handler)

        Assert.assertNull(data.anchorTrackingDisposable)
        verify(anchor, never()).addOnLayoutChangeListener(any())
    }

    @Test
    fun `startAnchorPositionTracking does nothing when popup is hidden`() {
        whenever(popupWindow.isShowing).doReturn(false)
        val data = tooltipData()

        underTest.startAnchorPositionTracking(data, div2View, handler)

        Assert.assertNull(data.anchorTrackingDisposable)
        verify(anchor, never()).addOnLayoutChangeListener(any())
    }

    @Test
    fun `startAnchorPositionTracking replaces previous tracking`() {
        val data = startTracking()
        val first = data.anchorTrackingDisposable
        Assert.assertNotNull(first)

        underTest.startAnchorPositionTracking(data, div2View, handler)

        verify(anchor).removeOnLayoutChangeListener(layoutListenerCaptor.firstValue)
        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
        Assert.assertNotNull(data.anchorTrackingDisposable)
        Assert.assertNotSame(first, data.anchorTrackingDisposable)
    }

    @Test
    fun `modal popup does not set dismissAction`() {
        createPopup(tooltipData(mode = DivTooltipMode.Modal(DivTooltipModeModal())))

        verify(tooltipWrapper, never()).dismissAction = any()
        verify(popupWindow).isFocusable = true
    }

    @Test
    fun `createOnBackPressCallback returns null when accessibility disabled`() {
        Assert.assertNull(underTest.createOnBackPressCallback(tooltipData(withPopup = false)) {})
    }

    @Test
    fun `createOnBackPressCallback returns callback when accessibility enabled`() {
        AccessibilityStateProvider.touchExplorationEnabled = true
        val accessibilityEnabledController = DivTooltipViewController(
            errorCollectors,
            divTooltipViewBuilder,
            AccessibilityStateProvider(true),
        ) { _, _, _ -> popupWindow }
        var callback: OnBackPressedCallback? = null

        disableAssertions {
            callback = accessibilityEnabledController.createOnBackPressCallback(tooltipData(withPopup = false)) {}
        }

        Assert.assertNotNull(callback)
        Assert.assertTrue(callback!!.isEnabled)
    }

    private fun createPopup(
        data: TooltipData = tooltipData(withPopup = false),
        onDismiss: () -> Unit = {},
    ): TooltipData {
        underTest.createPopupWindow(data, onTouchOutside = {}, onDismiss = onDismiss)
        return data
    }

    private fun startTracking(data: TooltipData = tooltipData()): TooltipData {
        underTest.startAnchorPositionTracking(data, div2View, handler)
        return data
    }

    private fun tooltipData(
        substrateDiv: Div? = null,
        bringToTopId: String? = null,
        mode: DivTooltipMode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
        withPopup: Boolean = true,
    ) = TooltipData(
        id = "tooltip_id",
        scopeId = null,
        bindingContext = bindingContext,
        divTooltip = DivTooltip(
            div = div,
            id = "tooltip_id",
            position = DivTooltip.Position.RIGHT.asExpression(),
            substrateDiv = substrateDiv,
            bringToTopId = bringToTopId,
            mode = mode,
        ),
        anchor = anchor,
    ).apply {
        if (withPopup) {
            popupWindow = this@DivTooltipViewControllerTest.popupWindow
        }
    }

    private fun location(position: DivTooltip.Position): Point {
        val tooltip = DivTooltip(
            div = div,
            id = "id",
            offset = null,
            position = position.asExpression(),
        )
        return calcPopupLocation(tooltipView, anchor, tooltip, ExpressionResolver.EMPTY)
    }

    private fun assertEquals(expected: Pair<Int, Int>, actual: Point) {
        Assert.assertEquals(expected.first, actual.x)
        Assert.assertEquals(expected.second, actual.y)
    }
}
