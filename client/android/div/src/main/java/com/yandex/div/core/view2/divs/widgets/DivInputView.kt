package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.KeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.reuse.InputFocusTracker
import com.yandex.div.internal.widget.SuperLineHeightEditText
import com.yandex.div2.Div

@Mockable
internal class DivInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divInputStyle
) : SuperLineHeightEditText(context, attrs, defStyleAttr),
    DivHolderView<Div.Input> by DivHolderViewMixin(),
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

    private var editorActionListener: OnEditorActionListener? = null

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
        focusTracker?.inputFocusChanged(tag = tag, view = this, focused = focused)
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas, scrollX, scrollY) { super.draw(it) }
    }

    override fun setFocusable(focusable: Boolean) {
        _isFocusable = focusable
        val isFocusable = _isFocusable && enabled
        super.setFocusable(isFocusable)
        isFocusableInTouchMode = isFocusable
    }

    override fun setOnEditorActionListener(l: OnEditorActionListener?) {
        editorActionListener = l
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0 &&
            (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
        ) {
            val imeAction = imeOptions and EditorInfo.IME_MASK_ACTION
            editorActionListener?.onEditorAction(this, imeAction, event)?.let {
                return it
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val baseInputConnection = super.onCreateInputConnection(outAttrs) ?: return null
        if ((inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) return baseInputConnection
        return object : InputConnectionWrapper(baseInputConnection, true) {
            override fun sendKeyEvent(event: KeyEvent): Boolean {
                if ((event.keyCode == KeyEvent.KEYCODE_ENTER ||
                            event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) &&
                    event.action == KeyEvent.ACTION_DOWN
                ) {
                    val imeAction = imeOptions and EditorInfo.IME_MASK_ACTION
                    editorActionListener?.onEditorAction(this@DivInputView, imeAction, event)?.let {
                        return it
                    }
                }

                return super.sendKeyEvent(event)
            }

            //Enter key from soft keyboard
            override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
                if (text == "\n") {
                    val imeAction = imeOptions and EditorInfo.IME_MASK_ACTION
                    editorActionListener?.onEditorAction(this@DivInputView, imeAction, null)?.let{
                        return it
                    }
                }
                return super.commitText(text, newCursorPosition)
            }
        }
    }

    override fun setInputType(type: Int) {
        if (inputType == type) {
            return
        }

        super.setInputType(type)
    }

    override fun setKeyListener(keyListener: KeyListener?) {
        if (this.keyListener == keyListener) {
            return
        }
        super.setKeyListener(keyListener)
    }
}
