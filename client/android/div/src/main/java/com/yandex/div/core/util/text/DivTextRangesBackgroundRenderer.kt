package com.yandex.div.core.util.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.ReplacementSpan
import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.widthPx
import com.yandex.div.core.view2.spannable.BaselineShiftSpan
import com.yandex.div.core.view2.spannable.FontSizeSpan
import com.yandex.div.core.view2.spannable.LineHeightWithTopOffsetSpan
import com.yandex.div.core.view2.spannable.VerticalAlignmentSpan
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import kotlin.math.max
import kotlin.math.min

internal class DivTextRangesBackgroundRenderer(
    private val view: View,
    private val resolver: ExpressionResolver,
) {

    private val paint = TextPaint()

    fun draw(
        canvas: Canvas,
        text: Spanned,
        layout: Layout,
        span: DivBackgroundSpan,
        start: Int,
        end: Int,
    ) {
        val metrics = view.resources.displayMetrics
        val drawer = BackgroundDrawer(metrics, span.border, span.background, canvas, resolver)
        RangeBoxes(text, layout, span, paint).build(start, end).forEach { box ->
            drawer.drawBackground(box.rect, box.roundsLeft, box.roundsRight)
        }
    }
}

private val METRIC_SPAN = MetricAffectingSpan::class.java

private class RangeBox(val rect: RectF, val roundsLeft: Boolean, val roundsRight: Boolean)

private class VerticalMetrics(val ascent: Int, val descent: Int)

private class FragmentMetrics(val font: VerticalMetrics?, val baselineShift: Int)

private class RangeBoxes(
    private val text: Spanned,
    private val layout: Layout,
    private val span: DivBackgroundSpan,
    private val paint: TextPaint,
) {

    private val fontMetrics = Paint.FontMetricsInt()

    fun build(start: Int, end: Int): List<RangeBox> {
        val startLine = layout.getLineForOffset(start)
        val endLine = layout.getLineForOffset(end)
        val endsAtLineStart = endLine > startLine && end == layout.getLineStart(endLine)
        val lastLine = if (endsAtLineStart) endLine - 1 else endLine
        return (startLine..lastLine).flatMap { line ->
            val lineEnd = if (line == endLine) end else min(end, getVisibleLineEnd(line))
            val startX = if (line == startLine) {
                layout.getPrimaryHorizontal(start)
            } else {
                getLineEdge(line, isStart = true)
            }
            val endX = when {
                line == endLine -> layout.getPrimaryHorizontal(end)
                lineEnd < layout.getLineEnd(line) -> layout.getPrimaryHorizontal(lineEnd)
                else -> getLineEdge(line, isStart = false)
            }
            val boxes = getLineBoxes(
                line = line,
                start = max(start, layout.getLineStart(line)),
                end = lineEnd,
                startX = startX.toInt(),
                endX = endX.toInt(),
            )
            val rangeEdges = listOfNotNull(
                startX.toInt().takeIf { line == startLine },
                endX.toInt().takeIf { line == lastLine },
            ).map(Int::toFloat)
            boxes.map { RangeBox(it, it.left in rangeEdges, it.right in rangeEdges) }
        }
    }

    private fun getVisibleLineEnd(line: Int): Int {
        val lineStart = layout.getLineStart(line)
        var lineEnd = layout.getLineEnd(line)
        while (lineEnd > lineStart && text[lineEnd - 1].isWhitespace()) lineEnd--
        return lineEnd
    }

    private fun getLineEdge(line: Int, isStart: Boolean): Float {
        val isRtl = layout.getParagraphDirection(line) == Layout.DIR_RIGHT_TO_LEFT
        return if (isStart != isRtl) layout.getLineLeft(line) else layout.getLineRight(line)
    }

    private fun getLineBoxes(
        line: Int,
        start: Int,
        end: Int,
        startX: Int,
        endX: Int,
    ): List<RectF> {
        val boxes = mutableListOf<RectF>()
        val lineMetrics by lazy(LazyThreadSafetyMode.NONE) { getLineMetrics(line) }
        var fragmentStart = start
        var fragmentStartX = startX
        while (fragmentStart < end) {
            val fragmentEnd = text.nextSpanTransition(fragmentStart, end, METRIC_SPAN)
            val fragmentEndX = if (fragmentEnd == end) {
                endX
            } else {
                layout.getPrimaryHorizontal(fragmentEnd).toInt()
            }
            val fragment = getFragmentMetrics(fragmentStart, fragmentEnd)
            val (top, bottom) = getBounds(
                line = line,
                metrics = fragment.font ?: lineMetrics,
                baselineShift = fragment.baselineShift,
            )
            val box = RectF(
                min(fragmentStartX, fragmentEndX).toFloat(),
                top.toFloat(),
                max(fragmentStartX, fragmentEndX).toFloat(),
                bottom.toFloat(),
            )
            val previous = boxes.lastOrNull()
            when {
                box.isEmpty -> Unit
                previous != null && previous.hasSameBounds(box) -> previous.union(box)
                else -> boxes += box
            }
            fragmentStart = fragmentEnd
            fragmentStartX = fragmentEndX
        }
        return boxes
    }

    private fun getFragmentMetrics(start: Int, end: Int): FragmentMetrics {
        val spans = text.getSpans(start, end, METRIC_SPAN)
        paint.set(layout.paint)
        paint.baselineShift = 0
        spans.forEach { it.updateDrawState(paint) }

        val hasFontSize = spans.any { it is FontSizeSpan }
        val isShifted = hasFontSize || spans.none { it is VerticalAlignmentSpan }
        val baselineShift = if (isShifted) paint.baselineShift else 0
        paint.getFontMetricsInt(fontMetrics)
        val font = VerticalMetrics(fontMetrics.ascent, fontMetrics.descent).takeIf { hasFontSize }
        return FragmentMetrics(font, baselineShift)
    }

    private fun getLineMetrics(line: Int): VerticalMetrics {
        val lineStart = layout.getLineStart(line)
        val lineEnd = layout.getLineEnd(line)
        val topOffset = LineHeightWithTopOffsetSpan.getLineTopOffset(text, layout, line)
        val lineAscent = layout.getLineAscent(line) + topOffset
        if (text.getSpans(lineStart, lineEnd, BaselineShiftSpan::class.java).isEmpty()) {
            return VerticalMetrics(lineAscent, layout.getLineDescent(line))
        }
        var ascent = 0
        var descent = 0
        var shiftedAscent = 0
        var shiftedDescent = 0
        var runStart = lineStart
        while (runStart < lineEnd) {
            val runEnd = text.nextSpanTransition(runStart, lineEnd, METRIC_SPAN)
            val shift = measureRun(runStart, runEnd)
            ascent = min(ascent, fontMetrics.ascent)
            descent = max(descent, fontMetrics.descent)
            shiftedAscent = min(shiftedAscent, fontMetrics.ascent + min(shift, 0))
            shiftedDescent = max(shiftedDescent, fontMetrics.descent + max(shift, 0))
            runStart = runEnd
        }
        return VerticalMetrics(
            ascent = lineAscent + ascent - shiftedAscent,
            descent = layout.getLineDescent(line) + descent - shiftedDescent,
        )
    }

    private fun measureRun(start: Int, end: Int): Int {
        paint.set(layout.paint)
        paint.baselineShift = 0
        var replacement: ReplacementSpan? = null
        for (span in text.getSpans(start, end, METRIC_SPAN)) {
            when (span) {
                is ReplacementSpan -> replacement = span
                is VerticalAlignmentSpan -> Unit
                else -> span.updateMeasureState(paint)
            }
        }
        paint.getFontMetricsInt(fontMetrics)
        replacement?.getSize(paint, text, start, end, fontMetrics)
        return paint.baselineShift
    }

    private fun getBounds(
        line: Int,
        metrics: VerticalMetrics,
        baselineShift: Int,
    ): Pair<Int, Int> {
        var ascent = metrics.ascent
        var descent = metrics.descent
        span.lineHeight?.let { lineHeight ->
            val extraSpace = lineHeight - (descent - ascent)
            val extraTop = extraSpace / 2
            ascent -= extraTop
            descent += extraSpace - extraTop
        }
        val baseline = layout.getLineBaseline(line) + baselineShift
        return baseline + ascent to baseline + descent
    }
}

private fun RectF.hasSameBounds(other: RectF) = top == other.top && bottom == other.bottom

private class BackgroundDrawer(
    private val metrics: DisplayMetrics,
    private val border: DivTextRangeBorder?,
    private val background: DivTextRangeBackground?,
    private val canvas: Canvas,
    private val resolver: ExpressionResolver,
) {
    val borerPaint = Paint()
    val radii: FloatArray?

    init {
        if (border != null) {
            radii = border.getCornerRadii(metrics, resolver)
            borerPaint.style = Paint.Style.STROKE
            borerPaint.isAntiAlias = true
            borerPaint.strokeWidth = border.stroke.widthPx(resolver, metrics).toFloat()
            border.stroke?.color?.evaluate(resolver)?.let {
                borerPaint.color = it
            }
        } else radii = null
    }

    fun drawBackground(box: RectF, roundsLeft: Boolean, roundsRight: Boolean) {
        val boxRadii = radii?.let { radii ->
            val left = if (roundsLeft) radii[0] else 0f
            val right = if (roundsRight) radii[2] else 0f
            floatArrayOf(left, left, right, right, right, right, left, left)
        }
        drawBackground(boxRadii, box.left, box.top, box.right, box.bottom)
    }

    private fun drawBackground(radii: FloatArray?,
                               start: Float,
                               top: Float,
                               end: Float,
                               bottom: Float) {
        val rect = RectF()
        rect.set(start, top, end, bottom)

        when (val divBackground = background?.value()) {
            is DivSolidBackground -> {
                val backgroundPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = divBackground.color.evaluate(resolver)
                }
                canvas.drawPath(getPath(radii, rect), backgroundPaint)
            }
        }

        drawBorder(radii, start, top, end, bottom)
    }

    private fun drawBorder(radii: FloatArray?,
                           start: Float,
                           top: Float,
                           end: Float,
                           bottom: Float) {

        if (border?.stroke == null) return
        val rect = RectF()
        val halfWidth = border.stroke!!.widthPx(resolver, metrics) / 2f

        rect.set( max(0f, start + halfWidth),
            max(0f, top + halfWidth),
            max(0f, end - halfWidth),
            max(0f, bottom - halfWidth)
        )

        val borderRadii = radii?.clone()
        if (borderRadii != null) {
            for (i in borderRadii.indices) {
                borderRadii[i] = max(0f, radii[i] - halfWidth)
            }
        }
        canvas.drawPath(getPath(borderRadii, rect), borerPaint)
    }

    private fun getPath(radii: FloatArray?, rect: RectF): Path {
        val path = Path()
        path.reset()
        if (radii == null) {
            path.addRect(rect, Path.Direction.CW)
        } else {
            path.addRoundRect(rect, radii, Path.Direction.CW)
        }
        path.close()
        return path
    }
}

private fun DivTextRangeBorder.getCornerRadii(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): FloatArray {
    val radius = cornerRadius?.evaluate(resolver).dpToPx(metrics).toFloat()

    return floatArrayOf(
        radius, radius,
        radius, radius,
        radius, radius,
        radius, radius
    )
}
