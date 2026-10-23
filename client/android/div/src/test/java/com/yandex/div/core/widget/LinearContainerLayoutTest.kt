package com.yandex.div.core.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat.HORIZONTAL
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.test.testContextThemeWrapper
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LinearContainerLayoutTest {
    private val context = testContextThemeWrapper()
    private val container = LinearContainerLayout(context)

    @Test
    fun `vertical placement skips gone children and keeps item spacing`() {
        container.orientation = VERTICAL
        container.setItemSpacing(5)
        val first = addChild()
        addChild().visibility = View.GONE
        val last = addChild()

        layoutContainer()

        assertEquals(listOf(0, 15), listOf(first.top, last.top))
    }

    @Test
    fun `horizontal RTL placement reverses children with spacing`() {
        container.orientation = HORIZONTAL
        context.applicationInfo.flags = context.applicationInfo.flags or android.content.pm.ApplicationInfo.FLAG_SUPPORTS_RTL
        container.layoutDirection = View.LAYOUT_DIRECTION_RTL
        container.gravity = Gravity.LEFT or Gravity.TOP
        container.setItemSpacing(5)
        val first = addChild()
        val last = addChild()

        layoutContainer()

        assertEquals(listOf(25, 0), listOf(first.left, last.left))
    }

    @Test
    fun `vertical dividers reserve space and draw between measured children`() {
        val divider = RecordingDivider()
        container.orientation = VERTICAL
        container.dividerDrawable = divider
        container.showDividers = ShowSeparatorsMode.SHOW_BETWEEN
        val first = addChild()
        val last = addChild()
        layoutContainer()

        container.draw(Canvas())

        assertEquals(listOf(0, 12), listOf(first.top, last.top))
        assertEquals(listOf(Rect(49, 10, 51, 12)), divider.drawnBounds)
    }

    @Test
    fun `horizontal baseline alignment positions children by their baseline`() {
        container.orientation = HORIZONTAL
        container.gravity = Gravity.LEFT or Gravity.TOP
        val first = addChild(baseline = 8)
        val last = addChild(baseline = 4)

        layoutContainer()

        assertEquals(listOf(0, 4), listOf(first.top, last.top))
    }

    private fun addChild(baseline: Int = -1): View {
        val child = object : View(context) {
            override fun getBaseline() = baseline
        }
        child.layoutParams = DivLayoutParams(20, 10).apply {
            isBaselineAligned = baseline >= 0
        }
        container.addView(child)
        return child
    }

    private fun layoutContainer() {
        val exact = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY)
        container.measure(exact, exact)
        container.layout(0, 0, 100, 100)
    }

    private class RecordingDivider : Drawable() {
        val drawnBounds = mutableListOf<Rect>()
        override fun getIntrinsicWidth() = 2
        override fun getIntrinsicHeight() = 2
        override fun draw(canvas: Canvas) { drawnBounds += Rect(bounds) }
        override fun setAlpha(alpha: Int) = Unit
        override fun setColorFilter(colorFilter: ColorFilter?) = Unit
        override fun getOpacity() = PixelFormat.TRANSLUCENT
    }
}
