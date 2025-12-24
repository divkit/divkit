package com.yandex.div.core.view2.spannable

import android.graphics.Paint.FontMetricsInt
import android.text.Layout
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import androidx.annotation.Px
import javax.inject.Provider
import kotlin.math.roundToInt

internal class VerticalAlignmentSpan(
    @param:Px private val fontSize: Int,
    private val alignment: TextVerticalAlignment,
    private val layoutProvider: Provider<Layout>
) : MetricAffectingSpan() {

    private val fontMetrics = FontMetricsInt()

    override fun updateMeasureState(paint: TextPaint) {
        updateDrawState(paint)
    }

    override fun updateDrawState(paint: TextPaint) {
        val layout = layoutProvider.get() ?: return

        val spanned = layout.text as? Spanned ?: return
        val spanStart = spanned.getSpanStart(this)
        val spanEnd = spanned.getSpanEnd(this)

        if (spanStart < 0 || spanEnd < 0 || spanStart >= spanEnd) return

        val lineNumber = layout.getLineForOffset(spanStart)

        val lineTop = layout.getLineTop(lineNumber)
        val lineBottom = layout.getLineBottom(lineNumber)
        val lineBaseline = layout.getLineBaseline(lineNumber)

        val lineSpacing = if (lineNumber == layout.lineCount - 1) 0 else layout.spacingAdd.roundToInt()

        val lineAscent = lineTop - lineBaseline
        val lineDescent = lineBottom - lineBaseline - lineSpacing

        if (fontSize > 0) {
            paint.textSize = fontSize.toFloat()
        }
        paint.getFontMetricsInt(fontMetrics)

        when (alignment) {
            TextVerticalAlignment.TOP -> {
                paint.baselineShift += lineAscent - fontMetrics.ascent
            }

            TextVerticalAlignment.CENTER -> {
                val lineCenter = (lineAscent + lineDescent) / 2
                val textCenter = (fontMetrics.ascent + fontMetrics.descent) / 2
                paint.baselineShift += lineCenter - textCenter
            }

            TextVerticalAlignment.BASELINE -> Unit

            TextVerticalAlignment.BOTTOM -> {
                paint.baselineShift += lineDescent - fontMetrics.descent
            }
        }
    }
}