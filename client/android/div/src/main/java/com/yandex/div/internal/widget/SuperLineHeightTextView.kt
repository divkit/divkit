package com.yandex.div.internal.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.core.widget.FixedLineHeightHelper
import com.yandex.div.core.widget.FixedLineHeightView
import kotlin.math.ceil
import kotlin.math.max

/**
 * For some reason line height is ignored on one-line TextViews in Android.
 */
open class SuperLineHeightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle), FixedLineHeightView {

    private inline val visibleLineCount get() = minOf(lineCount, maxLines)
    private val fixedLineHeightHelper = FixedLineHeightHelper(this)

    override var fixedLineHeight by fixedLineHeightHelper::lineHeight

    var isTightenWidth = false
        set (value){
            val prevValue = field
            field = value
            if (prevValue != value) {
                requestLayout()
            }
        }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        fixedLineHeightHelper.onFontSizeChanged()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        fixedLineHeightHelper.measureWithFixedLineHeight(heightMeasureSpec, visibleLineCount) {
            super.setMeasuredDimension(measuredWidthAndState, it)
        }

        if (isTightenWidth && maxWidth > 0) {
            val linesCount = layout.lineCount
            var maxWidth = 0f
            repeat(linesCount) { n ->
                maxWidth = max(maxWidth, layout.getLineWidth(n));
            }
            maxWidth += compoundPaddingLeft + compoundPaddingRight
            val newWidth = ceil(maxWidth).toInt()
            if (newWidth < measuredWidth) {
                val newMeasuredWidthAndState = MeasureSpec.makeMeasureSpec(
                    newWidth,
                    MeasureSpec.getMode(measuredWidthAndState)
                )
                super.setMeasuredDimension(newMeasuredWidthAndState, measuredHeightAndState)
            }
        }
    }

    override fun getCompoundPaddingTop() = super.getCompoundPaddingTop() + fixedLineHeightHelper.extraPaddingTop

    override fun getCompoundPaddingBottom() =
        super.getCompoundPaddingBottom() + fixedLineHeightHelper.extraPaddingBottom
}
