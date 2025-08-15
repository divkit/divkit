package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.yandex.div.core.widget.FixedLineHeightHelper
import com.yandex.div.core.widget.FixedLineHeightView

internal open class SuperLineHeightEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), FixedLineHeightView {

    private var horizontalScrollingEnabled = false
    private var isTextFitting = true
    private var isDisallowInterceptTouchEvent = false
    private val interceptTouchEventNeeded get() = !horizontalScrollingEnabled && !isTextFitting

    private inline val visibleLineCount get() = when {
        lineCount == 0 -> 1
        lineCount > maxLines -> maxLines
        else -> lineCount
    }

    private val fixedLineHeightHelper = FixedLineHeightHelper(this)

    override var fixedLineHeight by fixedLineHeightHelper::lineHeight

    private var currentLineCount = 0

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        fixedLineHeightHelper.onFontSizeChanged()
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            remeasureWrapContentConstrained()
        }
        updateFittingText()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        fixedLineHeightHelper.measureWithFixedLineHeight(heightMeasureSpec, visibleLineCount) {
            super.setMeasuredDimension(measuredWidthAndState, it)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateFittingText(h)
    }

    override fun getCompoundPaddingTop() = super.getCompoundPaddingTop() + fixedLineHeightHelper.extraPaddingTop

    override fun getCompoundPaddingBottom() =
        super.getCompoundPaddingBottom() + fixedLineHeightHelper.extraPaddingBottom

    override fun setHorizontallyScrolling(whether: Boolean) {
        horizontalScrollingEnabled = whether
        super.setHorizontallyScrolling(whether)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!interceptTouchEventNeeded) {
            if (isDisallowInterceptTouchEvent) {
                requestDisallowInterceptTouchEvent(false)
            }
            return super.onTouchEvent(event)
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun remeasureWrapContentConstrained() {
        if (layoutParams?.height != DivLayoutParams.WRAP_CONTENT_CONSTRAINED) {
            currentLineCount = visibleLineCount
            return
        }

        if (currentLineCount != visibleLineCount) {
            currentLineCount = visibleLineCount
            requestLayout()
        }
    }

    private fun updateFittingText(h: Int = height) {
        if (layout == null || h == 0) return
        val availableSize = h - compoundPaddingTop - compoundPaddingBottom
        val targetSize = textHeight(layout.lineCount)
        isTextFitting = availableSize >= targetSize
    }

    private fun requestDisallowInterceptTouchEvent(enabled: Boolean) {
        isDisallowInterceptTouchEvent = enabled
        parent.requestDisallowInterceptTouchEvent(enabled)
    }
}
