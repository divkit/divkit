package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import kotlin.math.roundToInt

private const val UNDEFINED = -1

internal open class SuperLineHeightEditText constructor(context: Context) : AppCompatEditText(context) {

    private var fixedLineHeight = UNDEFINED

    private var extraPaddingTop = 0
    private var extraPaddingBottom = 0

    private var verticallyScrolling = true

    private val visibleLineCount get() = when {
        lineCount == 0 -> 1
        lineCount > maxLines -> maxLines
        else -> lineCount
    }

    fun setFixedLineHeight(lineHeight: Int?) {
        fixedLineHeight = lineHeight ?: UNDEFINED
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        extraPaddingTop = (add / 2).roundToInt()
        extraPaddingBottom = (add / 2).toInt()
        super.setLineSpacing(add, mult)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (fixedLineHeight == UNDEFINED ||
            MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            return
        }

        val fixedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            fixedLineHeight * visibleLineCount + paddingTop + paddingBottom,
            MeasureSpec.getMode(measuredHeightAndState)
        )
        setMeasuredDimension(measuredWidthAndState, fixedHeightMeasureSpec)
    }

    override fun getCompoundPaddingTop() = super.getCompoundPaddingTop() + extraPaddingTop

    override fun getCompoundPaddingBottom() = super.getCompoundPaddingBottom() + extraPaddingBottom

    override fun setHorizontallyScrolling(whether: Boolean) {
        verticallyScrolling = !whether
        super.setHorizontallyScrolling(whether)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!verticallyScrolling) {
            return super.onTouchEvent(event)
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP -> parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onTouchEvent(event)
    }
}
