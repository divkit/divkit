package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.accessibility.AccessibilityManager
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

    private val accessibilityManager = context.getSystemService(
        Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

    private var _hint: String? = null

    init {
        setPadding(0, 0, 0, 0)
    }

    fun setInputHint(hint: String?) {
        _hint = hint
        this.hint = when {
            accessibilityManager?.isTouchExplorationEnabled != true -> hint
            hint.isNullOrEmpty() && contentDescription.isNullOrEmpty() -> null
            hint.isNullOrEmpty() -> contentDescription
            contentDescription.isNullOrEmpty() -> hint
            else -> "${hint.trimEnd('.')}. $contentDescription"
        }
    }

    override fun setContentDescription(contentDescription: CharSequence?) {
        super.setContentDescription(contentDescription)
        setInputHint(_hint)
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
