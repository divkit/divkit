package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.reuse.InputFocusTracker
import com.yandex.div.internal.widget.SuperLineHeightEditText
import com.yandex.div2.DivInput

@Mockable
internal class DivInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SuperLineHeightEditText(context, attrs),
    DivHolderView<DivInput> by DivHolderViewMixin(),
    DivAnimator {

    @get:DrawableRes
    private val nativeBackgroundResId: Int
        get() {
            return TypedValue().let { value ->
                context.theme.resolveAttribute(android.R.attr.editTextBackground, value, true)
                value.resourceId
            }
        }

    internal val nativeBackground = ContextCompat.getDrawable(context, nativeBackgroundResId)

    internal var focusTracker: InputFocusTracker? = null

    private val onTextChangedActions = mutableListOf<(Editable?) -> Unit>()

    private var textChangeWatcher: TextWatcher? = null

    private var _hint: String? = null

    private var _isFocusable = true

    var enabled = true
        internal set(value) {
            field = value
            isFocusable = _isFocusable
        }

    internal var accessibilityEnabled: Boolean = false
        set(value) {
            field = value
            setInputHint(_hint)
        }

    fun setInputHint(hint: String?) {
        _hint = hint
        this.hint = when {
            !accessibilityEnabled -> hint
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

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        tag?.let { focusTracker?.inputFocusChanged(tag = it, focused = focused) }
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClippedAndTranslated(canvas, scrollX, scrollY) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClippedAndTranslated(canvas, scrollX, scrollY) { super.dispatchDraw(it) }
    }

    override fun setFocusable(focusable: Boolean) {
        _isFocusable = focusable
        val isFocusable = _isFocusable && enabled
        super.setFocusable(isFocusable)
        isFocusableInTouchMode = isFocusable
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
