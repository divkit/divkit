package com.yandex.div.core.util.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.text.Layout
import android.util.DisplayMetrics
import androidx.annotation.ColorInt
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.unitToPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivCloudBackground
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

internal class CloudTextRangeBackgroundRenderer(
    private val context: Context,
    private val expressionResolver: ExpressionResolver
) : DivTextRangesBackgroundRenderer() {

    private val path = Path()
    private val paint = Paint()

    private val displayMetrics: DisplayMetrics
        get() = context.resources.displayMetrics

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        border: DivTextRangeBorder?,
        background: DivTextRangeBackground?
    ) {
        val cloudBackground = background?.value() as? DivCloudBackground ?: return
        draw(canvas, layout, startLine, endLine, startOffset, endOffset, cloudBackground)
    }

    private fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        background: DivCloudBackground
    ) {
        val metrics = displayMetrics
        val fillColor = background.color.evaluate(expressionResolver)
        val cornerRadius = background.cornerRadius.evaluate(expressionResolver).dpToPx(metrics)
        val unit = background.paddings?.unit?.evaluate(expressionResolver) ?: DivSizeUnit.DP
        val padding = Rect(
            background.paddings?.left?.evaluate(expressionResolver)?.unitToPx(metrics, unit) ?: 0,
            background.paddings?.top?.evaluate(expressionResolver)?.unitToPx(metrics, unit) ?: 0,
            background.paddings?.right?.evaluate(expressionResolver)?.unitToPx(metrics, unit) ?: 0,
            background.paddings?.bottom?.evaluate(expressionResolver)?.unitToPx(metrics, unit) ?: 0
        )
        val lines = buildLines(layout, startLine, endLine, startOffset, endOffset, cornerRadius, padding)

        if (lines.size < 2) {
            drawLines(canvas, lines, 0, lines.size, cornerRadius, fillColor)
        } else {
            var componentStart = 0
            var lineCount = 1
            for (i in 0 until lines.size - 1) {
                if (lines[i].left > lines[i + 1].right) {
                    drawLines(canvas, lines, componentStart, lineCount, cornerRadius, fillColor)
                    componentStart = i + 1
                    lineCount = 0
                }
                lineCount++
            }
            drawLines(canvas, lines, componentStart, lineCount, cornerRadius, fillColor)
        }
    }

    private fun buildLines(
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        cornerRadius: Int,
        padding: Rect
    ): Array<Rect> {
        val lastLine = endLine - startLine
        val lineCount = lastLine + 1
        if (lineCount == 0) return emptyArray()

        val lines = Array(lineCount) { index ->
            Rect(
                (if (index == 0) startOffset else layout.getLineLeft(startLine + index).roundToInt()) - padding.left,
                layout.getLineTop(startLine + index) - padding.top,
                (if (index == lastLine) endOffset else layout.getLineRight(startLine + index).roundToInt()) + padding.right,
                layout.getLineBottom(startLine + index) + padding.bottom
            )
        }

        coalesceInvisibleLeftBounds(lines)
        coalesceInvisibleRightBounds(lines)

        val leftBounds = IntArray(lineCount)
        val rightBounds = IntArray(lineCount)
        for (i in lines.indices) {
            leftBounds[i] = -lines[i].left
            rightBounds[i] = lines[i].right
        }

        coalesceCloseBounds(leftBounds, cornerRadius * 2)
        coalesceCloseBounds(rightBounds, cornerRadius * 2)

        for (i in lines.indices) {
            lines[i].left = -leftBounds[i]
            lines[i].right = rightBounds[i]
        }
        return lines
    }

    private fun coalesceInvisibleLeftBounds(lines: Array<Rect>) {
        val lineCount = lines.size
        var x = lines.first().left
        var y = lines.first().top
        for (i in lines.indices) {
            val line = lines[i]
            var visibleLineHeight = line.bottom - y
            var j = i + 1
            var nextX = Int.MIN_VALUE
            while (j < lineCount && lines[j].top < line.bottom) {
                if (lines[j].left <= line.left) {
                    visibleLineHeight -= line.bottom - lines[j].top
                    nextX = lines[j].left
                    break
                }
                j++
            }
            if (visibleLineHeight <= 0) {
                line.left = max(x, nextX)
                visibleLineHeight = 0
            } else {
                x = line.left
            }
            y += visibleLineHeight
        }
    }

    private fun coalesceInvisibleRightBounds(lines: Array<Rect>) {
        val lineCount = lines.size
        var x = lines.first().right
        var y = lines.first().top
        for (i in lines.indices) {
            val line = lines[i]
            var visibleLineHeight = line.bottom - y
            var j = i + 1
            var nextX = Int.MAX_VALUE
            while (j < lineCount && lines[j].top < line.bottom) {
                if (lines[j].right >= line.right) {
                    visibleLineHeight -= line.bottom - lines[j].top
                    nextX = lines[j].right
                    break
                }
                j++
            }
            if (visibleLineHeight <= 0) {
                line.right = min(x, nextX)
                visibleLineHeight = 0
            } else {
                x = line.right
            }
            y += visibleLineHeight
        }
    }

    private fun coalesceCloseBounds(lines: IntArray, minDelta: Int) {
        var boundCoalesced: Boolean
        var pass = 0
        do {
            boundCoalesced = false
            val indices = if (pass % 2 == 0) lines.indices else lines.indices.reversed()
            for (i in indices) {
                val upDelta = if (i == 0) 0 else lines[i - 1] - lines[i]
                val downDelta = if (i == lines.lastIndex) 0 else lines[i + 1] - lines[i]
                val isStable = (upDelta <= 0 || upDelta >= minDelta) && (downDelta <= 0 || downDelta >= minDelta)
                if (isStable) {
                    if (i != 0) {
                        if (upDelta < 0 && abs(upDelta) < minDelta) {
                            lines[i - 1] = lines[i]
                            boundCoalesced = true
                        }
                    }
                    if (i != lines.lastIndex) {
                        if (downDelta < 0 && abs(downDelta) < minDelta) {
                            lines[i + 1] = lines[i]
                            boundCoalesced = true
                        }
                    }
                }
            }
            pass++
        } while (boundCoalesced)
    }

    private fun drawLines(
        canvas: Canvas,
        lines: Array<Rect>,
        start: Int,
        lineCount: Int,
        cornerRadius: Int,
        @ColorInt fillColor: Int
    ) {
        if (lineCount < 1) return

        val firstLine = lines[start]
        val lastLine = lines[start + lineCount - 1]
        val leftSegments = buildLeftSegments(lines, start, lineCount)
        val rightSegments = buildRightSegments(lines, start, lineCount)
        path.reset()

        val baseRadius = cornerRadius.toFloat()
        var inRadius = min(baseRadius, min(firstLine.width() / 2.0f, rightSegments[OFFSET_SEGMENT_DELTA_Y] / 2.0f))
        var outRadius = 0.0f
        path.moveTo(firstLine.right.toFloat() - inRadius, firstLine.top.toFloat())
        path.rQuadTo(0.9f * inRadius, 0.1f * inRadius, inRadius, inRadius)
        for (i in rightSegments.indices step SEGMENT_VALUES) {
            val isLastSegment = i >= rightSegments.size - SEGMENT_VALUES
            val deltaX = rightSegments[i + OFFSET_SEGMENT_DELTA_X].toFloat()
            val deltaY = rightSegments[i + OFFSET_SEGMENT_DELTA_Y].toFloat()
            val nextDeltaY = if (isLastSegment) 0.0f else rightSegments[i + SEGMENT_VALUES + OFFSET_SEGMENT_DELTA_Y].toFloat()

            outRadius = min(baseRadius, min(abs(deltaX) / 2.0f, deltaY / 2.0f))
            path.rLineTo(0.0f, deltaY - inRadius - outRadius)
            path.rQuadTo(0.1f * inRadius * deltaX.sign, 0.9f * outRadius, outRadius * deltaX.sign, outRadius)
            if (!isLastSegment) {
                inRadius = min(baseRadius, min(abs(deltaX) / 2.0f, nextDeltaY / 2.0f))
                path.rLineTo(deltaX - (inRadius + outRadius) * deltaX.sign, 0.0f)
                path.rQuadTo(0.9f * inRadius * deltaX.sign, 0.1f * inRadius, inRadius * deltaX.sign, inRadius)
            }
        }

        inRadius = min(baseRadius, min(lastLine.width() / 2.0f, -leftSegments[OFFSET_SEGMENT_DELTA_Y] / 2.0f))
        path.rLineTo(-lastLine.width().toFloat() + outRadius + inRadius, 0.0f)
        path.rQuadTo(-0.9f * inRadius, -0.1f * inRadius, -inRadius, -inRadius)
        for (i in leftSegments.indices step SEGMENT_VALUES) {
            val isLastSegment = i >= leftSegments.size - SEGMENT_VALUES
            val deltaX = leftSegments[i + OFFSET_SEGMENT_DELTA_X].toFloat()
            val deltaY = leftSegments[i + OFFSET_SEGMENT_DELTA_Y].toFloat()
            val nextDeltaY = if (isLastSegment) 0.0f  else leftSegments[i + SEGMENT_VALUES + OFFSET_SEGMENT_DELTA_Y].toFloat()

            outRadius = min(baseRadius, min(abs(deltaX) / 2.0f, -deltaY / 2.0f))
            path.rLineTo(0.0f, deltaY + inRadius + outRadius)
            path.rQuadTo(0.1f * outRadius * deltaX.sign, -0.9f * outRadius, outRadius * deltaX.sign, -outRadius)
            if (!isLastSegment) {
                inRadius = min(baseRadius, min(abs(deltaX) / 2.0f, -nextDeltaY / 2.0f))
                path.rLineTo(deltaX - (inRadius + outRadius) * deltaX.sign, 0.0f)
                path.rQuadTo(0.9f * inRadius * deltaX.sign, -0.1f * inRadius, inRadius * deltaX.sign, -inRadius)
            }
        }
        path.close()

        paint.color = fillColor
        canvas.drawPath(path, paint)
    }

    private fun buildLeftSegments(lines: Array<Rect>, start: Int, lineCount: Int): IntArray {
        val end = start + lineCount - 1
        if (lineCount == 1) {
            return IntArray(2).apply {
                set(OFFSET_SEGMENT_DELTA_X, lines[end].width())
                set(OFFSET_SEGMENT_DELTA_Y, -lines[end].height())
            }
        }

        var x = lines[end].left
        var y = lines[end].bottom
        val segments = IntArray(lineCount * SEGMENT_VALUES)
        var segmentIndex = 0
        for (i in (start..end).reversed()) {
            val line = lines[i]
            if (line.left != x) {
                segments[SEGMENT_VALUES * segmentIndex + OFFSET_SEGMENT_DELTA_X] = line.left - x
                x = line.left
                segmentIndex++
            }

            var deltaY = line.top - y
            var j = i - 1
            while (j >= start && lines[j].bottom > line.top) {
                if (lines[j].left <= line.left) {
                    deltaY -= line.top - lines[j].bottom
                    break
                }
                j--
            }
            deltaY = deltaY.coerceAtMost(0)
            segments[SEGMENT_VALUES * segmentIndex + OFFSET_SEGMENT_DELTA_Y] += deltaY
            y += deltaY
        }
        segments[SEGMENT_VALUES * segmentIndex + OFFSET_SEGMENT_DELTA_X] = lines[start].width()

        return segments.copyOf((segmentIndex + 1) * SEGMENT_VALUES)
    }

    private fun buildRightSegments(lines: Array<Rect>, start: Int, lineCount: Int): IntArray {
        val end = start + lineCount - 1
        if (lineCount == 1) {
            return IntArray(2).apply {
                set(OFFSET_SEGMENT_DELTA_X, -lines[start].width())
                set(OFFSET_SEGMENT_DELTA_Y, lines[start].height())
            }
        }

        var x = lines[start].right
        var y = lines[start].top
        val segments = IntArray(lineCount * SEGMENT_VALUES)
        var segmentIndex = 0
        for (i in start..end) {
            val line = lines[i]
            if (line.right != x) {
                segments[SEGMENT_VALUES * segmentIndex + OFFSET_SEGMENT_DELTA_X] = line.right - x
                x = line.right
                segmentIndex++
            }

            var deltaY = line.bottom - y
            var j = i + 1
            while (j <= end && lines[j].top < line.bottom) {
                if (lines[j].right >= line.right) {
                    deltaY -= line.bottom - lines[j].top
                    break
                }
                j++
            }
            deltaY = deltaY.coerceAtLeast(0)
            segments[SEGMENT_VALUES * segmentIndex + OFFSET_SEGMENT_DELTA_Y] += deltaY
            y += deltaY
        }
        segments[SEGMENT_VALUES * segmentIndex + OFFSET_SEGMENT_DELTA_X] = -lines[end].width()

        return segments.copyOf((segmentIndex + 1) * SEGMENT_VALUES)
    }

    private companion object {
        const val SEGMENT_VALUES = 2
        const val OFFSET_SEGMENT_DELTA_X = 0
        const val OFFSET_SEGMENT_DELTA_Y = 1
    }
}
