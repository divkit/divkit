package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.widget.SelectView
import com.yandex.div2.Div


@Mockable
internal class DivSelectView(context: Context) : SelectView(context),
    DivHolderView<Div.Select> by DivHolderViewMixin(),
    DivAnimator {

    var valueUpdater: ((String) -> Unit)? = null

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        onBoundsChanged(width, height)
    }
}
