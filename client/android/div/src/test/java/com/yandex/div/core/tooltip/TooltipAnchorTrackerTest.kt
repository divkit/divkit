package com.yandex.div.core.tooltip

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import com.yandex.div.core.asExpression
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests for [TooltipAnchorTracker].
 */
@RunWith(RobolectricTestRunner::class)
class TooltipAnchorTrackerTest {

    private val layoutListeners = mutableListOf<View.OnLayoutChangeListener>()
    private val preDrawListeners = mutableListOf<ViewTreeObserver.OnPreDrawListener>()
    private val locationX = AtomicInteger(100)
    private val locationY = AtomicInteger(200)
    private val anchorWidth = AtomicInteger(300)
    private val anchorHeight = AtomicInteger(100)

    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { isAlive } doReturn true
        on { addOnPreDrawListener(any()) } doAnswer { inv ->
            preDrawListeners.add(inv.arguments[0] as ViewTreeObserver.OnPreDrawListener)
            null
        }
        on { removeOnPreDrawListener(any()) } doAnswer { inv ->
            preDrawListeners.remove(inv.arguments[0] as ViewTreeObserver.OnPreDrawListener)
            null
        }
    }

    private val anchor = mock<View> {
        on { viewTreeObserver } doReturn viewTreeObserver
        on { width } doAnswer { anchorWidth.get() }
        on { height } doAnswer { anchorHeight.get() }
        on { getLocationInWindow(any()) } doAnswer { inv ->
            val location = inv.arguments[0] as IntArray
            location[0] = locationX.get()
            location[1] = locationY.get()
            null
        }
        on { addOnLayoutChangeListener(any()) } doAnswer { inv ->
            layoutListeners.add(inv.arguments[0] as View.OnLayoutChangeListener)
            null
        }
        on { removeOnLayoutChangeListener(any()) } doAnswer { inv ->
            layoutListeners.remove(inv.arguments[0] as View.OnLayoutChangeListener)
            null
        }
    }

    private val popupWindow = mock<SafePopupWindow> {
        on { isShowing } doReturn true
    }

    private val tooltipData = TooltipData(
        id = "tooltip_id",
        scopeId = null,
        bindingContext = BindingContext(mock<Div2View>(), ExpressionResolver.EMPTY),
        divTooltip = DivTooltip(
            div = Div.Text(DivText(text = "tooltip".asExpression())),
            id = "tooltip_id",
            position = DivTooltip.Position.RIGHT.asExpression(),
        ),
        popupWindow = popupWindow,
        anchor = anchor,
        onBackPressedCallback = null,
    )

    private val handler = Handler(Looper.getMainLooper())
    private val positionChangedCount = AtomicInteger(0)
    private val onAnchorPositionChanged = { positionChangedCount.incrementAndGet(); Unit }

    private val underTest = TooltipAnchorTracker(
        tooltip = tooltipData,
        popupWindow = popupWindow,
        handler = handler,
        onAnchorPositionChanged = onAnchorPositionChanged,
    )

    @Test
    fun `notifies on initial position`() {
        Assert.assertEquals(1, positionChangedCount.get())
    }

    @Test
    fun `notifies when anchor location changes`() {
        positionChangedCount.set(0)

        locationX.set(150)
        notifyLayoutChanged()

        Assert.assertEquals(1, positionChangedCount.get())
    }

    @Test
    fun `notifies when anchor size changes`() {
        positionChangedCount.set(0)

        anchorWidth.set(320)
        notifyLayoutChanged()

        Assert.assertEquals(1, positionChangedCount.get())
    }

    @Test
    fun `does not notify when position and size are unchanged`() {
        positionChangedCount.set(0)

        notifyLayoutChanged()

        Assert.assertEquals(0, positionChangedCount.get())
    }

    @Test
    fun `does not notify when tooltip is dismissed`() {
        positionChangedCount.set(0)
        tooltipData.dismissed = true

        locationX.set(150)
        notifyLayoutChanged()

        Assert.assertEquals(0, positionChangedCount.get())
    }

    @Test
    fun `does not notify when popup is not showing`() {
        positionChangedCount.set(0)
        whenever(popupWindow.isShowing).doReturn(false)

        locationX.set(150)
        notifyLayoutChanged()

        Assert.assertEquals(0, positionChangedCount.get())
    }

    @Test
    fun `stops tracking after default duration`() {
        Assert.assertTrue(layoutListeners.isNotEmpty())
        Assert.assertTrue(preDrawListeners.isNotEmpty())

        ShadowLooper.idleMainLooper(ANCHOR_TRACKING_DURATION_MS, TimeUnit.MILLISECONDS)

        Assert.assertTrue(layoutListeners.isEmpty())
        Assert.assertTrue(preDrawListeners.isEmpty())
    }

    @Test
    fun `keeps tracking before duration elapses`() {
        ShadowLooper.idleMainLooper(ANCHOR_TRACKING_DURATION_MS - 1, TimeUnit.MILLISECONDS)

        Assert.assertTrue(layoutListeners.isNotEmpty())
        Assert.assertTrue(preDrawListeners.isNotEmpty())
    }

    @Test
    fun `does not notify after duration elapsed`() {
        ShadowLooper.idleMainLooper(ANCHOR_TRACKING_DURATION_MS, TimeUnit.MILLISECONDS)
        positionChangedCount.set(0)

        locationX.set(150)
        notifyLayoutChanged()

        Assert.assertEquals(0, positionChangedCount.get())
    }

    @Test
    fun `close removes listeners and cancels timeout`() {
        underTest.close()

        Assert.assertTrue(layoutListeners.isEmpty())
        Assert.assertTrue(preDrawListeners.isEmpty())

        ShadowLooper.idleMainLooper(ANCHOR_TRACKING_DURATION_MS, TimeUnit.MILLISECONDS)

        Assert.assertTrue(layoutListeners.isEmpty())
        Assert.assertTrue(preDrawListeners.isEmpty())
    }

    @Test
    fun `does not notify after close`() {
        underTest.close()
        positionChangedCount.set(0)

        locationX.set(150)
        notifyLayoutChanged()

        Assert.assertEquals(0, positionChangedCount.get())
    }

    @Test
    fun `close is idempotent`() {
        underTest.close()
        underTest.close()

        Assert.assertTrue(layoutListeners.isEmpty())
        Assert.assertTrue(preDrawListeners.isEmpty())
    }

    private fun notifyLayoutChanged() {
        layoutListeners.toList().forEach { listener ->
            listener.onLayoutChange(anchor, 0, 0, 300, 100, 0, 0, 300, 100)
        }
    }
}
