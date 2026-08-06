package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.SelectView

internal class DivSelectView(context: Context) : SelectView(context),
    DivHolderView<DivBlock.Select> by DivHolderViewMixin(),
    DivAnimator {

    var valueUpdater: ((String) -> Unit)? = null

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        onBoundsChanged(width, height)
    }
}
