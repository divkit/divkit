package com.yandex.div.core.view2.spannable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.Layout
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.LineBackgroundSpan
import androidx.annotation.Px
import androidx.core.util.Pools
import java.util.LinkedList
import java.util.Queue
import javax.inject.Provider
import kotlin.math.roundToInt

internal class VerticalAlignmentSpan(
    @Px private val fontSize: Int,
    private val alignment: TextVerticalAlignment,
    private val layoutProvider: Provider<Layout>
) : CharacterStyle(), LineBackgroundSpan {

    private val fontMetrics = FontMetricsInt()
    private val lines: Queue<IntArray> = LinkedList()
    private var textDrawWasCalled = false

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        if (textDrawWasCalled) {
            lines.clear()
        }
        textDrawWasCalled = false

        val spanned = text as? Spanned ?: return
        val spanStart = spanned.getSpanStart(this)
        val spanEnd = spanned.getSpanEnd(this)

        if (start <= spanEnd && spanStart <= end) {
            val layout = layoutProvider.get()
            val lineSpacing = if (lineNumber == layout.lineCount - 1) 0 else layout.spacingAdd.roundToInt()
            val line = LINE_POOL.acquire() ?: IntArray(2)
            line[INDEX_LINE_ASCENT] = top - baseline
            line[INDEX_LINE_DESCENT] = bottom - baseline - lineSpacing
            lines += line
        }
    }

    override fun updateDrawState(paint: TextPaint) {
        textDrawWasCalled = true
        if (lines.isEmpty()) {
            return
        }

        val line = lines.remove()
        val lineAscent = line[INDEX_LINE_ASCENT]
        val lineDescent = line[INDEX_LINE_DESCENT]
        LINE_POOL.release(line)

        if (fontSize > 0) {
            paint.textSize = fontSize.toFloat()
        }
        paint.getFontMetricsInt(fontMetrics)

        when (alignment) {
            TextVerticalAlignment.TOP -> {
                paint.baselineShift += lineAscent - fontMetrics.ascent
            }

            TextVerticalAlignment.CENTER ->  {
                val lineCenter =  (lineAscent + lineDescent) / 2
                val textCenter =  (fontMetrics.ascent + fontMetrics.descent) / 2
                paint.baselineShift += lineCenter - textCenter
            }

            TextVerticalAlignment.BASELINE -> Unit

            TextVerticalAlignment.BOTTOM -> {
                paint.baselineShift += lineDescent - fontMetrics.descent
            }
        }
    }

    private companion object {

        private val LINE_POOL = Pools.SimplePool<IntArray>(16)

        private const val INDEX_LINE_ASCENT = 0
        private const val INDEX_LINE_DESCENT = 1
    }
}
