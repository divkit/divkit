package com.yandex.div.core.widget

import android.os.Build
import android.view.View.MeasureSpec
import android.widget.TextView
import com.yandex.div.core.widget.FixedLineHeightView.Companion.UNDEFINED_LINE_HEIGHT
import com.yandex.div.internal.widget.fontHeight
import com.yandex.div.internal.widget.fontHeightInt
import com.yandex.div.internal.widget.textHeight
import kotlin.math.min

internal class FixedLineHeightHelper(private val view: TextView) {

    private var textPaddingTop = 0
    private var textPaddingBottom = 0

    val extraPaddingTop: Int
        get() = textPaddingTop

    val extraPaddingBottom: Int
        get() = textPaddingBottom

    var lineHeight = UNDEFINED_LINE_HEIGHT
        set (value) {
            if (field == value) return
            field = value
            applyLineHeight(value)
        }

    init {
        view.includeFontPadding = false
    }

    fun onFontSizeChanged() {
        applyLineHeight(lineHeight)
    }

    private fun applyLineHeight(lineHeight: Int) {
        if (lineHeight == UNDEFINED_LINE_HEIGHT) {
            resetLineHeight()
            return
        }

        val textPadding = lineHeight - view.fontHeightInt
        if (textPadding < 0) {
            textPaddingTop = textPadding / 2
            textPaddingBottom = textPadding - textPaddingTop
        } else {
            textPaddingBottom = textPadding / 2
            textPaddingTop = textPadding - textPaddingBottom
        }

        val lineSpacing = lineHeight - view.fontHeight
        view.setLineSpacing(lineSpacing, 1f)
        setFallbackLineSpacing(false)
    }

    private fun resetLineHeight() {
        textPaddingTop = 0
        textPaddingBottom = 0
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

        val textPadding = if (visibleLineCount >= view.lineCount) textPaddingTop + textPaddingBottom else 0
        val textHeight = view.textHeight(visibleLineCount) + textPadding
        val viewHeight = (textHeight + view.paddingTop + view.paddingBottom).coerceAtLeast(view.minimumHeight)

        val measureSpec = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            makeAtMostSpec(min(viewHeight, MeasureSpec.getSize(heightMeasureSpec)))
        } else {
            makeExactSpec(viewHeight)
        }
        applySpec(measureSpec)
    }
}
