package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.SelectView
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivSelect


@Mockable
internal class DivSelectView constructor(context: Context) : SelectView(context), DivAnimator,
    DivBorderSupports by DivBorderSupportsMixin(), TransientView by TransientViewMixin(), ExpressionSubscriber {

    var div: DivSelect? = null

    var valueUpdater: ((String) -> Unit)? = null

    override val subscriptions = mutableListOf<Disposable>()

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }

    override fun release() {
        super.release()
        releaseBorderDrawer()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        onBoundsChanged(width, height)
    }
}
