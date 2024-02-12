package com.yandex.div.internal.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.core.widget.FixedLineHeightHelper
import com.yandex.div.core.widget.FixedLineHeightView

/**
 * For some reason line height is ignored on one-line TextViews in Android.
 */
open class SuperLineHeightTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context!!, attrs, defStyle), FixedLineHeightView {

    private inline val visibleLineCount get() = minOf(lineCount, maxLines)
    private val fixedLineHeightHelper = FixedLineHeightHelper(this)

    override var fixedLineHeight by fixedLineHeightHelper::lineHeight

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        fixedLineHeightHelper.measureWithFixedLineHeight(heightMeasureSpec, visibleLineCount) {
            super.setMeasuredDimension(measuredWidthAndState, it)
        }
    }

    override fun getCompoundPaddingTop() = super.getCompoundPaddingTop() + fixedLineHeightHelper.extraPaddingTop

    override fun getCompoundPaddingBottom() =
        super.getCompoundPaddingBottom() + fixedLineHeightHelper.extraPaddingBottom
}
