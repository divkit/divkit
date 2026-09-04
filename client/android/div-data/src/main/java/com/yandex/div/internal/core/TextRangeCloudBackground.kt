package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

private const val SEGMENT_VALUES = 2
private const val SEGMENT_DELTA_X = 0
private const val SEGMENT_DELTA_Y = 1

@InternalApi
object TextRangeCloudBackground {
    data class LineBounds(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
    )

    interface PathSink {
        fun moveTo(x: Float, y: Float)
        fun relativeLineTo(dx: Float, dy: Float)
        fun relativeQuadraticTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float)
        fun close()
    }

    fun buildPath(
        sourceLines: List<LineBounds>,
        cornerRadius: Int,
        sink: PathSink,
        mergeCloseBounds: Boolean = true,
    ) {
        if (sourceLines.isEmpty()) return

        val lines = sourceLines.map { MutableLineBounds(it.left, it.top, it.right, it.bottom) }
        coalesceInvisibleLeftBounds(lines)
        coalesceInvisibleRightBounds(lines)

        if (mergeCloseBounds) {
            val leftBounds = IntArray(lines.size) { -lines[it].left }
            val rightBounds = IntArray(lines.size) { lines[it].right }
            coalesceCloseBounds(leftBounds, cornerRadius * 2)
            coalesceCloseBounds(rightBounds, cornerRadius * 2)
            lines.forEachIndexed { index, line ->
                line.left = -leftBounds[index]
                line.right = rightBounds[index]
            }
        }

        var componentStart = 0
        var componentLineCount = 1
        for (index in 0 until lines.lastIndex) {
            val current = lines[index]
            val next = lines[index + 1]
            if (current.left > next.right || current.right < next.left) {
                sink.addComponent(lines, componentStart, componentLineCount, cornerRadius)
                componentStart = index + 1
                componentLineCount = 0
            }
            componentLineCount++
        }
        sink.addComponent(lines, componentStart, componentLineCount, cornerRadius)
    }

    private fun coalesceInvisibleLeftBounds(lines: List<MutableLineBounds>) {
        var x = lines.first().left
        var y = lines.first().top
        for (index in lines.indices) {
            val line = lines[index]
            var visibleLineHeight = line.bottom - y
            var nextIndex = index + 1
            var nextX = Int.MIN_VALUE
            while (nextIndex < lines.size && lines[nextIndex].top < line.bottom) {
                if (lines[nextIndex].left <= line.left) {
                    visibleLineHeight -= line.bottom - lines[nextIndex].top
                    nextX = lines[nextIndex].left
                    break
                }
                nextIndex++
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

    private fun coalesceInvisibleRightBounds(lines: List<MutableLineBounds>) {
        var x = lines.first().right
        var y = lines.first().top
        for (index in lines.indices) {
            val line = lines[index]
            var visibleLineHeight = line.bottom - y
            var nextIndex = index + 1
            var nextX = Int.MAX_VALUE
            while (nextIndex < lines.size && lines[nextIndex].top < line.bottom) {
                if (lines[nextIndex].right >= line.right) {
                    visibleLineHeight -= line.bottom - lines[nextIndex].top
                    nextX = lines[nextIndex].right
                    break
                }
                nextIndex++
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

    private fun coalesceCloseBounds(bounds: IntArray, minDelta: Int) {
        var coalesced: Boolean
        var pass = 0
        do {
            coalesced = false
            val indices = if (pass % 2 == 0) bounds.indices else bounds.indices.reversed()
            for (index in indices) {
                val previousDelta = if (index == 0) 0 else bounds[index - 1] - bounds[index]
                val nextDelta = if (index == bounds.lastIndex) 0 else bounds[index + 1] - bounds[index]
                val isStable = (previousDelta <= 0 || previousDelta >= minDelta) &&
                    (nextDelta <= 0 || nextDelta >= minDelta)
                if (isStable && index != 0 && previousDelta < 0 && abs(previousDelta) < minDelta) {
                    bounds[index - 1] = bounds[index]
                    coalesced = true
                }
                if (isStable && index != bounds.lastIndex && nextDelta < 0 && abs(nextDelta) < minDelta) {
                    bounds[index + 1] = bounds[index]
                    coalesced = true
                }
            }
            pass++
        } while (coalesced)
    }

    private fun PathSink.addComponent(
        lines: List<MutableLineBounds>,
        start: Int,
        lineCount: Int,
        cornerRadius: Int,
    ) {
        if (lineCount < 1) return

        val firstLine = lines[start]
        val lastLine = lines[start + lineCount - 1]
        val leftSegments = buildLeftSegments(lines, start, lineCount)
        val rightSegments = buildRightSegments(lines, start, lineCount)
        val baseRadius = cornerRadius.toFloat()
        var inRadius = min(baseRadius, min(firstLine.width / 2f, rightSegments[SEGMENT_DELTA_Y] / 2f))
        var outRadius = 0f
        moveTo(firstLine.right - inRadius, firstLine.top.toFloat())
        relativeQuadraticTo(0.9f * inRadius, 0.1f * inRadius, inRadius, inRadius)
        for (index in rightSegments.indices step SEGMENT_VALUES) {
            val isLastSegment = index >= rightSegments.size - SEGMENT_VALUES
            val deltaX = rightSegments[index + SEGMENT_DELTA_X].toFloat()
            val deltaY = rightSegments[index + SEGMENT_DELTA_Y].toFloat()
            val nextDeltaY = if (isLastSegment) {
                0f
            } else {
                rightSegments[index + SEGMENT_VALUES + SEGMENT_DELTA_Y].toFloat()
            }
            outRadius = min(baseRadius, min(abs(deltaX) / 2f, deltaY / 2f))
            relativeLineTo(0f, deltaY - inRadius - outRadius)
            relativeQuadraticTo(
                0.1f * inRadius * deltaX.sign,
                0.9f * outRadius,
                outRadius * deltaX.sign,
                outRadius,
            )
            if (!isLastSegment) {
                inRadius = min(baseRadius, min(abs(deltaX) / 2f, nextDeltaY / 2f))
                relativeLineTo(deltaX - (inRadius + outRadius) * deltaX.sign, 0f)
                relativeQuadraticTo(
                    0.9f * inRadius * deltaX.sign,
                    0.1f * inRadius,
                    inRadius * deltaX.sign,
                    inRadius,
                )
            }
        }

        inRadius = min(baseRadius, min(lastLine.width / 2f, -leftSegments[SEGMENT_DELTA_Y] / 2f))
        relativeLineTo(-lastLine.width + outRadius + inRadius, 0f)
        relativeQuadraticTo(-0.9f * inRadius, -0.1f * inRadius, -inRadius, -inRadius)
        for (index in leftSegments.indices step SEGMENT_VALUES) {
            val isLastSegment = index >= leftSegments.size - SEGMENT_VALUES
            val deltaX = leftSegments[index + SEGMENT_DELTA_X].toFloat()
            val deltaY = leftSegments[index + SEGMENT_DELTA_Y].toFloat()
            val nextDeltaY = if (isLastSegment) {
                0f
            } else {
                leftSegments[index + SEGMENT_VALUES + SEGMENT_DELTA_Y].toFloat()
            }
            outRadius = min(baseRadius, min(abs(deltaX) / 2f, -deltaY / 2f))
            relativeLineTo(0f, deltaY + inRadius + outRadius)
            relativeQuadraticTo(
                0.1f * outRadius * deltaX.sign,
                -0.9f * outRadius,
                outRadius * deltaX.sign,
                -outRadius,
            )
            if (!isLastSegment) {
                inRadius = min(baseRadius, min(abs(deltaX) / 2f, -nextDeltaY / 2f))
                relativeLineTo(deltaX - (inRadius + outRadius) * deltaX.sign, 0f)
                relativeQuadraticTo(
                    0.9f * inRadius * deltaX.sign,
                    -0.1f * inRadius,
                    inRadius * deltaX.sign,
                    -inRadius,
                )
            }
        }
        close()
    }

    private fun buildLeftSegments(
        lines: List<MutableLineBounds>,
        start: Int,
        lineCount: Int,
    ): IntArray {
        val end = start + lineCount - 1
        if (lineCount == 1) return intArrayOf(lines[end].width, -lines[end].height)

        var x = lines[end].left
        var y = lines[end].bottom
        val segments = IntArray(lineCount * SEGMENT_VALUES)
        var segmentIndex = 0
        for (index in (start..end).reversed()) {
            val line = lines[index]
            if (line.left != x) {
                segments[SEGMENT_VALUES * segmentIndex + SEGMENT_DELTA_X] = line.left - x
                x = line.left
                segmentIndex++
            }
            var deltaY = line.top - y
            var previousIndex = index - 1
            while (previousIndex >= start && lines[previousIndex].bottom > line.top) {
                if (lines[previousIndex].left <= line.left) {
                    deltaY -= line.top - lines[previousIndex].bottom
                    break
                }
                previousIndex--
            }
            deltaY = deltaY.coerceAtMost(0)
            segments[SEGMENT_VALUES * segmentIndex + SEGMENT_DELTA_Y] += deltaY
            y += deltaY
        }
        segments[SEGMENT_VALUES * segmentIndex + SEGMENT_DELTA_X] = lines[start].width
        return segments.copyOf((segmentIndex + 1) * SEGMENT_VALUES)
    }

    private fun buildRightSegments(
        lines: List<MutableLineBounds>,
        start: Int,
        lineCount: Int,
    ): IntArray {
        val end = start + lineCount - 1
        if (lineCount == 1) return intArrayOf(-lines[start].width, lines[start].height)

        var x = lines[start].right
        var y = lines[start].top
        val segments = IntArray(lineCount * SEGMENT_VALUES)
        var segmentIndex = 0
        for (index in start..end) {
            val line = lines[index]
            if (line.right != x) {
                segments[SEGMENT_VALUES * segmentIndex + SEGMENT_DELTA_X] = line.right - x
                x = line.right
                segmentIndex++
            }
            var deltaY = line.bottom - y
            var nextIndex = index + 1
            while (nextIndex <= end && lines[nextIndex].top < line.bottom) {
                if (lines[nextIndex].right >= line.right) {
                    deltaY -= line.bottom - lines[nextIndex].top
                    break
                }
                nextIndex++
            }
            deltaY = deltaY.coerceAtLeast(0)
            segments[SEGMENT_VALUES * segmentIndex + SEGMENT_DELTA_Y] += deltaY
            y += deltaY
        }
        segments[SEGMENT_VALUES * segmentIndex + SEGMENT_DELTA_X] = -lines[end].width
        return segments.copyOf((segmentIndex + 1) * SEGMENT_VALUES)
    }

    private data class MutableLineBounds(
        var left: Int,
        val top: Int,
        var right: Int,
        val bottom: Int,
    ) {
        val width: Int get() = right - left
        val height: Int get() = bottom - top
    }
}
