package com.yandex.div.core.view2.spannable

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

internal class LineMetricsSpan : LineBackgroundSpan {

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = false
        strokeWidth = 0.0f
    }

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
        linePaint.color = 0xFF003049.toInt()
        var line = top.f
        canvas.drawLine(left.f, line, right.f, line, linePaint)

        linePaint.color = 0xFFF77F00.toInt()
        linePaint.pathEffect = DashPathEffect(floatArrayOf(8.0f, 4.0f, 1.0f, 4.0f), 0.0f)
        line = top + (bottom - top) / 2.0f
        canvas.drawLine(left.f, line, right.f, line, linePaint)

        linePaint.color = 0xFFD62828.toInt()
        linePaint.pathEffect = null
        line = (baseline - 1).f
        canvas.drawLine(left.f, line, right.f, line, linePaint)

        linePaint.color = 0xFFFCBF49.toInt()
        line = (bottom - 1).f
        canvas.drawLine(left.f, line, right.f, line, linePaint)
    }
}

private val Int.f: Float
    inline get() = toFloat()
