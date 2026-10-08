package com.yandex.div.core.tooltip

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.FrameContainerLayout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DivTooltipContainerTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val overlayChild = View(context)
    private val overlayView = FrameContainerLayout(context).apply {
        addView(overlayChild, DivLayoutParams(MATCH_PARENT, MATCH_PARENT))
    }
    private val container = DivTooltipContainer(context).apply {
        setViews(substrate = View(context), bringToTop = overlayView, tooltip = View(context))
    }

    @Before
    fun setUp() {
        layoutWindow(width = 500, height = 1000)
    }

    @Test
    fun `bring to top child is measured to source bounds`() {
        container.setBringToTopPosition(x = 10, y = 20, width = 200, height = 80)
        layoutWindow(width = 500, height = 1000)

        assertEquals(Rect(0, 0, 200, 80), overlayChild.bounds)
    }

    @Test
    fun `window resize preserves source bounds for bring to top child`() {
        container.setBringToTopPosition(x = 10, y = 20, width = 200, height = 80)

        layoutWindow(width = 700, height = 300)

        assertEquals(Rect(0, 0, 200, 80), overlayChild.bounds)
    }

    @Test
    fun `source resize updates bring to top child bounds`() {
        container.setBringToTopPosition(x = 10, y = 20, width = 200, height = 80)

        container.setBringToTopPosition(x = 30, y = 40, width = 300, height = 100)
        layoutWindow(width = 500, height = 1000)

        assertEquals(Rect(0, 0, 300, 100), overlayChild.bounds)
    }

    @Test
    fun `tracked tooltip bounds constrain measurement after popup relayout`() {
        val tooltipView = View(context).apply {
            layoutParams = DivLayoutParams(width = 800, height = 1200)
        }
        container.setViews(substrate = View(context), bringToTop = null, tooltip = tooltipView)

        container.setTooltipPosition(x = 30, y = 40, width = 300, height = 400)
        layoutWindow(width = 500, height = 1000)

        assertEquals(300, tooltipView.measuredWidth)
        assertEquals(400, tooltipView.measuredHeight)
        assertEquals(Rect(30, 40, 330, 440), tooltipView.bounds)
    }

    private val View.bounds: Rect
        get() = Rect(left, top, right, bottom)

    private fun layoutWindow(width: Int, height: Int) {
        container.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
        )
        container.layout(0, 0, width, height)
    }
}
