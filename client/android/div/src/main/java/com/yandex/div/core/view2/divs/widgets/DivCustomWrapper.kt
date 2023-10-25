package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div2.DivCustom

internal class DivCustomWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameContainerLayout(context, attrs, defStyleAttr),
    DivHolderView<DivCustom> by DivHolderViewMixin() {

    val customView get() = if (isNotEmpty()) get(0) else null

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
