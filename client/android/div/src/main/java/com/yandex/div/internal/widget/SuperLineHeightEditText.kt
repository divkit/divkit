package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.yandex.div.core.util.extractMaxHeight
import kotlin.math.roundToInt

private const val UNDEFINED = -1

internal open class SuperLineHeightEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var fixedLineHeight = UNDEFINED

    private var extraPaddingTop = 0
    private var extraPaddingBottom = 0

    private var verticallyScrolling = true

    private val visibleLineCount get() = when {
        lineCount == 0 -> 1
        lineCount > maxLines -> maxLines
        else -> lineCount
    }

    private var currentLineCount = 0

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            addTextChangedListener { remeasureWrapContentConstrained() }
        }
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

        val maxHeight = extractMaxHeight(heightMeasureSpec)
        var resultHeight = minOf(
            maxHeight,
            fixedLineHeight * visibleLineCount + paddingTop + paddingBottom
        )
        resultHeight = maxOf(minimumHeight, resultHeight)
        val fixedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            resultHeight,
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
