package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.internal.widget.slider.SliderView
import com.yandex.div2.DivSlider

internal class DivSliderView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SliderView(context, attrs, defStyleAttr),
    DivHolderView<DivSlider> by DivHolderViewMixin() {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }
}
