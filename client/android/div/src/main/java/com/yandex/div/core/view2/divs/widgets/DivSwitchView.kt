package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.SwitchView

internal class DivSwitchView(context: Context) : SwitchView(context),
    DivHolderView<DivBlock.Switch> by DivHolderViewMixin() {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }
}
