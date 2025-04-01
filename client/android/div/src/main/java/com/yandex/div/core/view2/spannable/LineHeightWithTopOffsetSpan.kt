package com.yandex.div.core.view2.spannable

import android.graphics.Paint
import android.text.Spanned
import android.text.style.LineHeightSpan
import androidx.annotation.Px
import androidx.core.text.getSpans
import kotlin.math.max

private const val NOT_SET = Int.MAX_VALUE

/**
 * Span with applying line height by text lines not paragraph.
 * Adds top offset to a first line of range text that span belongs to.
 */
internal class LineHeightWithTopOffsetSpan(
    @Px private val topOffset: Int,
    @Px private val lineHeight: Int,
    private val topOffsetStart: Int,
    private val topOffsetEnd: Int,
) : LineHeightSpan {

    private var fontMetricsSaved = false
    private var savedTop: Int = NOT_SET
    private var savedAscent: Int = NOT_SET
    private var savedDescent: Int = NOT_SET
    private var savedBottom: Int = NOT_SET

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

        if (start > spanEnd || spanStart > end) return

        if (fontMetricsSaved) {
            restoreFontMetrics(fm)
        } else {
            fontMetricsSaved = true
            saveFontMetrics(fm)
        }

        val maxLineHeight = spanned.getSpans<LineHeightWithTopOffsetSpan>(start, end)
            .fold(lineHeight) { result, span -> max(result, span.lineHeight) }
        applyLineHeight(maxLineHeight, fm)

        if (topOffsetStart == spanStart && topOffsetStart in start..end) {
            applyTopOffset(fm)
        }
        if (text.substring(start, end).contains("\n")) {
            // Next line on new paragraph
            fontMetricsSaved = false
        }
    }

    private fun applyLineHeight(lineHeight: Int, fm: Paint.FontMetricsInt) {
        if (lineHeight <= 0) {
            return
        }
        val targetLineHeight = lineHeight
        val originLineHeight = fm.descent - fm.ascent
        val topAscent = fm.top - fm.ascent
        val bottomDescent = fm.bottom - fm.descent
        if (originLineHeight >= 0) {
            val extraHeight = targetLineHeight - originLineHeight
            if (extraHeight < 0) {
                fm.ascent = (fm.ascent - extraHeight / 2).coerceAtMost(0)
                fm.descent = (fm.ascent + targetLineHeight).coerceAtLeast(0)
            } else {
                fm.descent = (fm.descent + extraHeight / 2).coerceAtLeast(0)
                fm.ascent = (fm.descent - targetLineHeight).coerceAtMost(0)
            }
            fm.top = fm.ascent + topAscent
            fm.bottom = fm.descent + bottomDescent
        }
    }

    private fun applyTopOffset(fm: Paint.FontMetricsInt) {
        if (topOffset <= 0) {
            return
        }
        fm.top -= topOffset
        fm.ascent -= topOffset
    }

    private fun saveFontMetrics(fm: Paint.FontMetricsInt) {
        savedTop = fm.top
        savedAscent = fm.ascent
        savedDescent = fm.descent
        savedBottom = fm.bottom
    }

    private fun restoreFontMetrics(fm: Paint.FontMetricsInt) {
        fm.top = savedTop
        fm.ascent = savedAscent
        fm.descent = savedDescent
        fm.bottom = savedBottom
    }
}
