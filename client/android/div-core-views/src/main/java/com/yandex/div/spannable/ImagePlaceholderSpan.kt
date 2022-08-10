package com.yandex.div.spannable

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.style.ReplacementSpan
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Placeholder image span for reserving space
 */
class ImagePlaceholderSpan constructor(
    private val width: Int,
    private val height: Int,
    private val yOffset: Float = 0f
) : ReplacementSpan() {
    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        fm?.apply {
            if (start == 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                top = 0
                ascent = 0
                bottom = 0
                descent = 0
                leading = 0
            }

            val desiredFmTop = ceil(height - yOffset).toInt()
            fm.ascent = min(-desiredFmTop, fm.ascent)
            fm.top = min(-desiredFmTop, fm.top)
            val desiredFmBottom = ceil(yOffset).toInt()
            fm.descent = max(desiredFmBottom, fm.descent)
            fm.bottom = max(desiredFmBottom, fm.bottom)
            fm.leading = fm.descent - fm.ascent
        }

        return width
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        start: Int, end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) = Unit
}
