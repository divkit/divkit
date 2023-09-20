package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.widget.wraplayout.WrapContainerLayout
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivContainer

internal class DivWrapLayout(context: Context) :
    WrapContainerLayout(context),
    DivAnimator,
    DivBorderSupports by DivBorderSupportsMixin(),
    TransientView by TransientViewMixin(),
    ExpressionSubscriber {

    internal var div: DivContainer? = null

    override val subscriptions = mutableListOf<Disposable>()

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

    override fun release() {
        super.release()
        releaseBorderDrawer()
    }
}
