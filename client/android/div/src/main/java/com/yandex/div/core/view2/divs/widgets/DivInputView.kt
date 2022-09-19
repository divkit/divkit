package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.view.SuperLineHeightEditText
import com.yandex.div2.DivBorder
import com.yandex.div2.DivInput

@Mockable
internal class DivInputView constructor(context: Context) : SuperLineHeightEditText(context),
    DivAnimator, DivBorderSupports, TransientView, ExpressionSubscriber {

    internal var div: DivInput? = null

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

    private var boundVariableTextWatcher: TextWatcher? = null

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        borderDrawer = updateBorderDrawer(border, resolver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderDrawer?.onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClippedAndTranslated(canvas, scrollX, scrollY) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (isDrawing) {
            super.dispatchDraw(canvas)
        } else {
            borderDrawer.drawClippedAndTranslated(canvas, scrollX, scrollY) {
                super.dispatchDraw(canvas)
            }
        }
    }

    override fun release() {
        super.release()
        borderDrawer?.release()
    }

    fun setBoundVariableChangeAction(action: (Editable?) -> Unit) {
        boundVariableTextWatcher = doAfterTextChanged { editable ->
            action(editable)
        }
    }

    fun removeBoundVariableChangeAction() {
        removeTextChangedListener(boundVariableTextWatcher)
        boundVariableTextWatcher = null
    }
}
