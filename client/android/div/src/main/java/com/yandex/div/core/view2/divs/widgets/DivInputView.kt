package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.SuperLineHeightEditText
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivInput

@Mockable
internal class DivInputView constructor(context: Context) : SuperLineHeightEditText(context),
    DivAnimator, DivBorderSupports by DivBorderSupportsMixin(), TransientView by TransientViewMixin(), ExpressionSubscriber {

    internal var div: DivInput? = null

    override val subscriptions = mutableListOf<Disposable>()

    private val onTextChangedActions = mutableListOf<(Editable?) -> Unit>()

    private var textChangeWatcher: TextWatcher? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClippedAndTranslated(canvas, scrollX, scrollY) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClippedAndTranslated(canvas, scrollX, scrollY) { super.dispatchDraw(it) }
    }

    override fun release() {
        super.release()
        releaseBorderDrawer()
    }

    fun addAfterTextChangeAction(action: (Editable?) -> Unit) {
        if (textChangeWatcher == null) {
            textChangeWatcher = doAfterTextChanged { editable ->
                onTextChangedActions.forEach { it.invoke(editable) }
            }
        }

        onTextChangedActions.add(action)
    }

    fun removeAfterTextChangeListener() {
        removeTextChangedListener(textChangeWatcher)
        onTextChangedActions.clear()
        textChangeWatcher = null
    }
}
