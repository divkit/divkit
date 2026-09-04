package com.yandex.div.core.util.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.text.Layout
import android.util.DisplayMetrics
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.unitToPx
import com.yandex.div.internal.core.TextRangeCloudBackground
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivCloudBackground
import com.yandex.div2.DivSizeUnit
import kotlin.math.roundToInt

internal class CloudTextRangeBackgroundRenderer(
    private val context: Context,
    private val expressionResolver: ExpressionResolver
) : DivTextRangesBackgroundRenderer() {

    private val path = Path()
    private val paint = Paint()
    private val pathSink = object : TextRangeCloudBackground.PathSink {
        override fun moveTo(x: Float, y: Float) = path.moveTo(x, y)
        override fun relativeLineTo(dx: Float, dy: Float) = path.rLineTo(dx, dy)
        override fun relativeQuadraticTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float) =
            path.rQuadTo(dx1, dy1, dx2, dy2)
        override fun close() = path.close()
    }

    private val displayMetrics: DisplayMetrics
        get() = context.resources.displayMetrics

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        span: DivBackgroundSpan,
    ) {
        val cloudBackground = span.background?.value() as? DivCloudBackground ?: return
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
        val lines = buildLines(layout, startLine, endLine, startOffset, endOffset, padding)
        path.reset()
        TextRangeCloudBackground.buildPath(lines, cornerRadius, pathSink)
        paint.color = fillColor
        canvas.drawPath(path, paint)
    }

    private fun buildLines(
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        padding: Rect,
    ): List<TextRangeCloudBackground.LineBounds> {
        val lastLine = endLine - startLine
        val lineCount = lastLine + 1
        if (lineCount == 0) return emptyList()

        return List(lineCount) { index ->
            TextRangeCloudBackground.LineBounds(
                left = (if (index == 0) startOffset else layout.getLineLeft(startLine + index).roundToInt()) -
                    padding.left,
                top = layout.getLineTop(startLine + index) - padding.top,
                right = (if (index == lastLine) endOffset else layout.getLineRight(startLine + index).roundToInt()) +
                    padding.right,
                bottom = layout.getLineBottom(startLine + index) + padding.bottom,
            )
        }
    }

}
