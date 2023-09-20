package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivContainer

internal class DivFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameContainerLayout(context, attrs, defStyleAttr), DivAnimator,
    DivBorderSupports by DivBorderSupportsMixin(), TransientView by TransientViewMixin(),
    ExpressionSubscriber {

    internal var div: DivContainer? = null

    override val subscriptions = mutableListOf<Disposable>()

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

    override fun release() {
        super.release()
        releaseBorderDrawer()
    }
}
