package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.widget.SuperLineHeightEditText
import com.yandex.div2.DivInput

@Mockable
internal class DivInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SuperLineHeightEditText(context, attrs),
    DivHolderView<DivInput> by DivHolderViewMixin(),
    DivAnimator {

    internal val nativeBackground: Drawable? = background

    private val onTextChangedActions = mutableListOf<(Editable?) -> Unit>()

    private var textChangeWatcher: TextWatcher? = null

    init {
        setPadding(0, 0, 0, 0)
    }

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
