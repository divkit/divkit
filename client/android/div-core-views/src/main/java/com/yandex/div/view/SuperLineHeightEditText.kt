package com.yandex.div.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import kotlin.math.roundToInt

private const val UNDEFINED = -1

open class SuperLineHeightEditText constructor(context: Context) : AppCompatEditText(context) {

    private var fixedLineHeight = UNDEFINED

    private var extraPaddingTop = 0
    private var extraPaddingBottom = 0

    private var singleLine = false

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

    override fun setSingleLine(isSingleLine: Boolean) {
        singleLine = isSingleLine
        super.setSingleLine(isSingleLine)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (singleLine) {
            return super.onTouchEvent(event)
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP -> parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onTouchEvent(event)
    }
}
