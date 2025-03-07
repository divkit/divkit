package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.widget.wraplayout.WrapContainerLayout
import com.yandex.div2.Div

internal class DivWrapLayout(context: Context) : WrapContainerLayout(context),
    DivHolderView<Div.Container> by DivHolderViewMixin(),
    DivCollectionHolder by DivCollectionHolderMixin(),
    DivAnimator {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(canvas) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(canvas) }
    }
}
