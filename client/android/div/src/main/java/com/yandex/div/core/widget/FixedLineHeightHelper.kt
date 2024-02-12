package com.yandex.div.core.widget

import android.os.Build
import android.view.View.MeasureSpec
import android.widget.TextView
import com.yandex.div.core.widget.FixedLineHeightView.Companion.UNDEFINED_LINE_HEIGHT
import com.yandex.div.internal.util.fontHeight
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class FixedLineHeightHelper(private val view: TextView) {

    var extraPaddingTop = 0
        private set
    var extraPaddingBottom = 0
        private set

    var lineHeight = UNDEFINED_LINE_HEIGHT
        set (value) {
            if (field == value) return
            field = value
            applyLineHeight(value)
        }

    init {
        view.includeFontPadding = false
    }

    private fun applyLineHeight(lineHeight: Int) {
        if (lineHeight == UNDEFINED_LINE_HEIGHT) {
            resetLineHeight()
            return
        }

        val lineSpacingExtra = lineHeight - view.fontHeight
        extraPaddingTop = (lineSpacingExtra / 2).toInt()
        extraPaddingBottom = (lineSpacingExtra / 2).roundToInt()
        view.setLineSpacing(lineSpacingExtra, 1f)
        setFallbackLineSpacing(false)
    }

    private fun resetLineHeight() {
        extraPaddingTop = 0
        extraPaddingBottom = 0
        view.setLineSpacing(0f, 1f)
        setFallbackLineSpacing(true)
    }

    private fun setFallbackLineSpacing(enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.isFallbackLineSpacing = enabled
        }
    }

    inline fun measureWithFixedLineHeight(
        heightMeasureSpec: Int,
        visibleLineCount: Int,
        applySpec: (Int) -> Unit
    ) {
        if (lineHeight == UNDEFINED_LINE_HEIGHT || isExact(heightMeasureSpec)) return

        val fixedHeight = max(
            lineHeight * visibleLineCount + view.paddingTop + view.paddingBottom,
            view.minimumHeight
        )
        val measureSpec = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            makeAtMostSpec(min(fixedHeight, MeasureSpec.getSize(heightMeasureSpec)))
        } else {
            makeExactSpec(fixedHeight)
        }
        applySpec(measureSpec)
    }
}
