package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivContainer

internal class DivFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameContainerLayout(context, attrs, defStyleAttr), DivAnimator, DivBorderSupports, TransientView, ExpressionSubscriber {

    internal var div: DivContainer? = null

    private var borderDrawer: DivBorderDrawer? = null
    override val border: DivBorder?
        get() = borderDrawer?.border

    override fun getDivBorderDrawer() = borderDrawer

    override var isTransient = false
        set(value) = invalidateAfter {
            field = value
        }

    override val subscriptions = mutableListOf<Disposable>()

    private var isDrawing = false

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        borderDrawer = updateBorderDrawer(border, resolver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderDrawer?.onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClipped(canvas) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)

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
}
