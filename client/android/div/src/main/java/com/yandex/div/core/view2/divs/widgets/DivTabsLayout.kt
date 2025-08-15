package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.internal.widget.tabs.TabsLayout
import com.yandex.div2.Div

@Mockable
internal class DivTabsLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TabsLayout(context, attrs),
    DivHolderView<Div.Tabs> by DivHolderViewMixin() {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(canvas) }
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }
}
