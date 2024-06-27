package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.widget.LinearContainerLayout
import com.yandex.div2.DivContainer

internal class DivLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearContainerLayout(context, attrs, defStyleAttr),
    DivHolderView<DivContainer> by DivHolderViewMixin(),
    DivCollectionHolder by DivCollectionHolderMixin(),
    DivAnimator {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }
}
