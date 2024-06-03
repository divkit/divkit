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
    @field:Px private val lineHeight: Int,
    @field:Px private val textLineHeight: Int = 0
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
        if (fontMetricsSaved) {
            restoreFontMetrics(fm)
        } else if (start >= spanStart) {
            fontMetricsSaved = true
            saveFontMetrics(fm)
        }
        if (start <= spanEnd && spanStart <= end) {  // segment intersection
            if (start >= spanStart && end <= spanEnd) {
                applyLineHeight(fm)
            } else if (lineHeight > textLineHeight) {
                applyLineHeight(fm)
            }
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
        val targetLineHeight = lineHeight
        val originLineHeight = fm.descent - fm.ascent
        val topAscent = fm.top - fm.ascent
        val bottomDescent = fm.bottom - fm.descent
        if (originLineHeight >= 0) {
            val ratio: Float = targetLineHeight * 1.0f / originLineHeight
            fm.descent = (fm.descent * ratio).roundToInt()
            fm.ascent = fm.descent - targetLineHeight
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
