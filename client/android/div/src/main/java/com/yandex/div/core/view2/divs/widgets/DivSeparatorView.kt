package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.SeparatorView

internal class DivSeparatorView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SeparatorView(context, attrs, defStyleAttr),
    DivHolderView<DivBlock.Separator> by DivHolderViewMixin() {

    init {
        dividerColor = DEFAULT_DIVIDER_COLOR
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
       onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    companion object {
        const val DEFAULT_DIVIDER_COLOR = 0x14000000
    }
}
