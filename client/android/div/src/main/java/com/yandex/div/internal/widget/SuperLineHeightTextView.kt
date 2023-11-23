package com.yandex.div.internal.widget

import android.content.Context
import android.util.AttributeSet
import com.yandex.div.core.util.extractMaxHeight
import kotlin.math.roundToInt

private const val UNDEFINED = -1

/**
 * For some reason line height is ignored on one-line TextViews in Android.
 */
internal open class SuperLineHeightTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : EllipsizedTextView(context!!, attrs, defStyle) {

    private var extraPaddingTop = 0
    private var extraPaddingBottom = 0
    private var shouldAddExtraSpacing = true

    private var fixedLineHeight = UNDEFINED
    private val visibleLineCount get() = minOf(lineCount, maxLines)

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
        if (fixedLineHeight != UNDEFINED && MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            measureWithFixedLineHeight(heightMeasureSpec)
        }
        lastMeasuredHeight = measuredHeight
    }

    private fun measureWithFixedLineHeight(heightMeasureSpec: Int) {
        val maxHeight = extractMaxHeight(heightMeasureSpec)
        val fixedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            minOf(maxHeight, fixedLineHeight * visibleLineCount + paddingBottom + paddingTop),
            MeasureSpec.getMode(heightMeasureSpec)
        )
        super.setMeasuredDimension(measuredWidthAndState, fixedHeightMeasureSpec)
    }

    override fun getCompoundPaddingTop(): Int {
        return super.getCompoundPaddingTop() + extraPaddingTop
    }

    override fun getCompoundPaddingBottom(): Int {
        return super.getCompoundPaddingBottom() + extraPaddingBottom
    }
}
