package com.yandex.div.internal.spannable

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.CallSuper
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Placeholder image span for reserving space
 */
internal class ImagePlaceholderSpan(
    private val width: Int,
    private val height: Int,
    private val lineHeight: Int = 0,
    private val fontSize: Int = 0
) : PositionAwareReplacementSpan() {

    @CallSuper
    override fun adjustSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm == null || lineHeight > 0) {
            return width
        }

        val imageOffset = getImageOffset(height, paint).roundToInt()
        val targetAscent = -height + imageOffset
        val targetDescent = targetAscent + height
        val topAscent = fm.top - fm.ascent
        val bottomDescent = fm.bottom - fm.descent

        fm.ascent = min(targetAscent, fm.ascent)
        fm.descent = max(targetDescent, fm.descent)
        fm.top = fm.ascent + topAscent
        fm.bottom = fm.descent + bottomDescent

        return width
    }

    private fun getImageOffset(imageHeight: Int, paint: Paint): Float {
        val textScale = if (fontSize > 0) fontSize / paint.textSize else 1.0f
        val textCenter = (paint.ascent() + paint.descent()) / 2.0f * textScale
        val imageCenter = -imageHeight.toFloat() / 2.0f
        return textCenter - imageCenter
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        start: Int, end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) = Unit
}
