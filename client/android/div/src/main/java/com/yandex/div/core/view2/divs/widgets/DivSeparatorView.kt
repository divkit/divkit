package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.internal.widget.SeparatorView
import com.yandex.div2.DivSeparator

internal class DivSeparatorView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SeparatorView(context, attrs, defStyleAttr),
    DivHolderView<DivSeparator> by DivHolderViewMixin() {

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
