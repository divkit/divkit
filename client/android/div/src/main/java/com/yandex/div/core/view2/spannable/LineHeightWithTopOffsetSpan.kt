package com.yandex.div.core.view2.spannable

import android.graphics.Paint
import android.text.Spanned
import android.text.style.LineHeightSpan
import androidx.annotation.Px
import kotlin.math.roundToInt

private const val NOT_SET = -1

/**
 * Span with applying line height by text lines not paragraph.
 * Adds top offset to a first line of range text that span belongs to.
 */
internal class LineHeightWithTopOffsetSpan(
    @field:Px private val topOffset: Int,
    @field:Px private val lineHeight: Int
) : LineHeightSpan {

    private var fontMetricsSaved = false
    private var savedAscent: Int = NOT_SET
    private var savedDescent: Int = NOT_SET
    private var savedTop: Int = NOT_SET

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lh: Int,
        fm: Paint.FontMetricsInt
    ) {
        val spanned = text as? Spanned ?: return
        val spanStart = spanned.getSpanStart(this)
        val spanEnd = spanned.getSpanEnd(this)
        if (fontMetricsSaved) {
            resetFontMetrics(fm)
        } else if (start >= spanStart) {
            fontMetricsSaved = true
            saveFontMetrics(fm)
        }
        if (start <= spanEnd && spanStart <= end) {
            applyLineHeight(fm)
        }
        if (spanStart in start..end) {
            applyTopOffset(fm)
        }
        if (text.substring(start, end).contains("\n")) {
            // Next line on new paragraph
            fontMetricsSaved = false
        }
    }

    private fun applyLineHeight(fm: Paint.FontMetricsInt) {
        if (lineHeight <= 0) {
            return
        }
        val originHeight = fm.descent - fm.ascent
        if (originHeight >= 0) {
            val ratio: Float = lineHeight * 1.0f / originHeight
            fm.descent = (fm.descent * ratio).roundToInt()
            fm.ascent = fm.descent - lineHeight
        }
    }

    private fun applyTopOffset(fm: Paint.FontMetricsInt) {
        if (topOffset <= 0) {
            return
        }
        fm.ascent -= topOffset
        fm.top -= topOffset
    }

    private fun saveFontMetrics(fm: Paint.FontMetricsInt) {
        savedAscent = fm.ascent
        savedDescent = fm.descent
        savedTop = fm.top
    }

    private fun resetFontMetrics(fm: Paint.FontMetricsInt) {
        fm.ascent = savedAscent
        fm.descent = savedDescent
        fm.top = savedTop
    }
}
