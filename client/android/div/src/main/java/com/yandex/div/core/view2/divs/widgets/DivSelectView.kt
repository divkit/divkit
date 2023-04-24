package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.SelectView
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivSelect


@Mockable
internal class DivSelectView constructor(context: Context) : SelectView(context), DivAnimator,
    DivBorderSupports, TransientView, ExpressionSubscriber {

    var div: DivSelect? = null

    var valueUpdater: ((String) -> Unit)? = null

    override val border: DivBorder?
        get() = borderDrawer?.border

    override var isTransient = false
        set(value) = invalidateAfter { field = value }

    override val subscriptions = mutableListOf<Disposable>()

    private var borderDrawer: DivBorderDrawer? = null

    private var isDrawing = false

    override fun getDivBorderDrawer() = borderDrawer

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        borderDrawer = updateBorderDrawer(border, resolver)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClipped(canvas) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (isDrawing) {
            super.dispatchDraw(canvas)
        } else {
            borderDrawer.drawClipped(canvas) { super.dispatchDraw(canvas) }
        }
    }

    override fun release() {
        super.release()
        borderDrawer?.release()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        borderDrawer?.onBoundsChanged(width, height)
    }
}
