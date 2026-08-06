package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.indicator.PagerIndicatorView

internal class DivPagerIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PagerIndicatorView(context, attrs, defStyleAttr),
    DivHolderView<DivBlock.Indicator> by DivHolderViewMixin() {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }
}
