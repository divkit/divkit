package com.yandex.div.core.tooltip

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.Assert

@Mockable
internal class DivTooltipContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): DivViewWrapper(context, attrs, defStyleAttr) {

    init {
        clipChildren = false
        clipToPadding = false
    }

    val tooltipView: View?
        get() = if (childCount == 0) null else getChildAt(0)

    fun updateLocation(x: Int, y: Int, width: Int, height: Int) {
        val tooltipView = this.tooltipView ?: return
        val layoutParams = tooltipView.layoutParams as? MarginLayoutParams ?: return

        layoutParams.leftMargin = x
        layoutParams.topMargin = y
        layoutParams.height = height
        layoutParams.width = width

        tooltipView.requestLayout()
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        if (childCount == 1) {
            Assert.fail("Adding more than one child view to DivTooltipContainer is not allowed.")
            return
        }
        super.addView(child, index, params)
    }
}
