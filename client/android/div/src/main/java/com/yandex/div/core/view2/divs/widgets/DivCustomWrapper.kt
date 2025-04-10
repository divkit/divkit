package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div2.Div

internal class DivCustomWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameContainerLayout(context, attrs, defStyleAttr),
    DivHolderView<Div.Custom> by DivHolderViewMixin() {

    val customView get() = if (isNotEmpty()) get(0) else null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    override fun getBaseline(): Int {
        return customView?.let { child ->
            child.baseline + child.marginTop + paddingTop
        } ?: super.getBaseline()
    }
}
