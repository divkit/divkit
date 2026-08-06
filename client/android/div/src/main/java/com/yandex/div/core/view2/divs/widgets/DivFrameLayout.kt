package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.FrameContainerLayout

internal class DivFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameContainerLayout(context, attrs, defStyleAttr),
    DivHolderView<DivBlock.Container> by DivHolderViewMixin(),
    DivCollectionHolder by DivCollectionHolderMixin(),
    DivAnimator {

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
}
