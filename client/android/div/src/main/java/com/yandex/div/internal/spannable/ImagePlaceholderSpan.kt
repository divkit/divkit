package com.yandex.div.internal.spannable

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.CallSuper
import com.yandex.div.core.view2.spannable.TextVerticalAlignment
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
    private val alignment: TextVerticalAlignment
) : PositionAwareReplacementSpan() {

    @CallSuper
    override fun adjustSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm == null || lineHeight > 0) {
            return width
        }

        val ascent = paint.ascent().roundToInt()
        val descent = paint.descent().roundToInt()

        val imageBaseline = when (alignment) {
            TextVerticalAlignment.TOP -> ascent + height
            TextVerticalAlignment.CENTER -> (ascent + descent + height) / 2
            TextVerticalAlignment.BASELINE -> 0
            TextVerticalAlignment.BOTTOM -> descent
        }
        val imageAscent = imageBaseline - height

        val topAscent = fm.top - fm.ascent
        val bottomDescent = fm.bottom - fm.descent

        fm.ascent = min(imageAscent, fm.ascent)
        fm.descent = max(imageBaseline, fm.descent)
        fm.top = fm.ascent + topAscent
        fm.bottom = fm.descent + bottomDescent

        return width
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        start: Int, end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) = Unit
}
