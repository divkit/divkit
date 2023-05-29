package com.yandex.div.internal.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import kotlin.math.roundToInt

/**
 * For some reason line height is ignored on one-line TextViews in Android.
 */
internal open class SuperLineHeightTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : EllipsizedTextView(context!!, attrs, defStyle) {

    private var lineSpacingExtraTop = 0
    private var lineSpacingExtraBottom = 0
    private var shouldAddExtraSpacing = true

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (!isInternalTextChange) {
            invalidateTextPadding()
        }
    }

    private fun invalidateTextPadding() {
        shouldAddExtraSpacing = true
        lineSpacingExtraTop = 0
        lineSpacingExtraBottom = 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (shouldReapplyExtraSpacing()) {
            invalidateTextPadding()
        }
        if (fixLineHeight()) {
            var measuredHeightAndState = measuredHeightAndState
            measuredHeightAndState = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(measuredHeightAndState) + lineSpacingExtraTop + lineSpacingExtraBottom,
                    MeasureSpec.getMode(measuredHeightAndState)
            )
            super.setMeasuredDimension(measuredWidthAndState, measuredHeightAndState)
        }
        lastMeasuredHeight = measuredHeight
    }

    private fun shouldReapplyExtraSpacing(): Boolean {
        val lastMeasuredHeight = lastMeasuredHeight
        if (lastMeasuredHeight == NOT_SET) {
            return false
        }
        return if (lineSpacingExtraTop == 0 && lineSpacingExtraBottom == 0) {
            false
        } else lastMeasuredHeight - measuredHeight != 0
    }

    override fun getCompoundPaddingTop(): Int {
        return super.getCompoundPaddingTop() + lineSpacingExtraTop
    }

    override fun getCompoundPaddingBottom(): Int {
        return super.getCompoundPaddingBottom() + lineSpacingExtraBottom
    }

    /**
     * For some reason line height is ignored on one-line TextViews in Android.
     *
     * Note: this method must be called after the width is measured
     */
    private fun fixLineHeight(): Boolean {
        val availableWidth = availableWidth()
        val text = text
        val textLayout = layout
        val lineSpacingExtra = lineSpacingExtra
        if (textLayout == null) {
            return false
        }
        if (shouldAddExtraSpacing && availableWidth > 0
                && lineSpacingExtra > 0
                && (!TextUtils.isEmpty(text) || !TextUtils.isEmpty(hint))
                && layout.lineCount == 1) {
            lineSpacingExtraTop = (lineSpacingExtra / 2f).roundToInt()
            lineSpacingExtraBottom = lineSpacingExtra.toInt() / 2
            shouldAddExtraSpacing = false
            return true
        }
        return false
    }
}
