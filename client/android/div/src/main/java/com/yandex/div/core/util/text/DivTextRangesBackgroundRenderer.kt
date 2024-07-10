package com.yandex.div.core.util.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.widthPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import kotlin.math.max
import kotlin.math.min

private const val DEFAULT_LINESPACING_EXTRA = 0f
private const val DEFAULT_LINESPACING_MULTIPLIER = 1f

internal abstract class DivTextRangesBackgroundRenderer() {
    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        border: DivTextRangeBorder?,
        background: DivTextRangeBackground?,
    )

    protected fun getLineTop(layout: Layout, line: Int): Int {
        var lineTop = layout.getLineTop(line)
        if (line == 0) {
            lineTop -= layout.topPadding
        }
        return lineTop
    }

    protected fun getLineBottom(layout: Layout, line: Int): Int {
        var lineBottom = layout.getLineBottomWithoutSpacing(line)
        if (line == layout.lineCount - 1) {
            lineBottom -= layout.bottomPadding
        }
        return lineBottom
    }

    private fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
        val lineBottom = getLineBottom(line)
        val isLastLine = line == lineCount - 1

        val lineBottomWithoutSpacing: Int
        val lineSpacingExtra = spacingAdd
        val lineSpacingMultiplier = spacingMultiplier
        val hasLineSpacing = lineSpacingExtra != DEFAULT_LINESPACING_EXTRA
            || lineSpacingMultiplier != DEFAULT_LINESPACING_MULTIPLIER

        if (!hasLineSpacing || isLastLine) {
            lineBottomWithoutSpacing = lineBottom
        } else {
            val extra: Float
            if (lineSpacingMultiplier.compareTo(DEFAULT_LINESPACING_MULTIPLIER) != 0) {
                val lineHeight = getLineHeight(line)
                extra = lineHeight - (lineHeight - lineSpacingExtra) / lineSpacingMultiplier
            } else {
                extra = lineSpacingExtra
            }

            lineBottomWithoutSpacing = (lineBottom - extra).toInt()
        }

        return lineBottomWithoutSpacing
    }

    private fun Layout.getLineHeight(line: Int): Int {
        return getLineTop(line + 1) - getLineTop(line)
    }
}

internal class SingleLineRenderer(
    private val view: View,
    private val resolver: ExpressionResolver,
) : DivTextRangesBackgroundRenderer() {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        border: DivTextRangeBorder?,
        background: DivTextRangeBackground?,
    ) {
        val lineTop = getLineTop(layout, startLine)
        val lineBottom = getLineBottom(layout, startLine)
        val left = min(startOffset, endOffset)
        val right = max(startOffset, endOffset)
        val borderDrawer = BackgroundDrawer(view.resources.displayMetrics, border, background, canvas, resolver)
        borderDrawer.drawBackground(left.toFloat(), lineTop.toFloat(), right.toFloat(), lineBottom.toFloat())
    }
}


internal class MultiLineRenderer(
    private val view: View,
    private val resolver: ExpressionResolver,
    ) : DivTextRangesBackgroundRenderer() {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        border: DivTextRangeBorder?,
        background: DivTextRangeBackground?,
    ) {
        val paragDir = layout.getParagraphDirection(startLine)
        val lineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine)
        } else {
            layout.getLineRight(startLine)
        }.toInt()

        var lineBottom = getLineBottom(layout, startLine)
        var lineTop = getLineTop(layout, startLine)
        val drawer = BackgroundDrawer(view.resources.displayMetrics, border, background, canvas, resolver)
        drawer.drawBackgroundStart(startOffset.toFloat(), lineTop.toFloat(),
            lineEndOffset.toFloat(), lineBottom.toFloat())

        for (line in startLine + 1 until endLine) {
            lineTop = getLineTop(layout, line)
            lineBottom = getLineBottom(layout, line)
            drawer.drawBackgroundMiddle(
                (layout.getLineLeft(line).toInt()).toFloat(),
                lineTop.toFloat(),
                (layout.getLineRight(line).toInt()).toFloat(),
                lineBottom.toFloat()
            )
        }

        val lineStartOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineRight(startLine)
        } else {
            layout.getLineLeft(startLine)
        }.toInt()

        lineBottom = getLineBottom(layout, endLine)
        lineTop = getLineTop(layout, endLine)

        drawer.drawBackgroundEnd(lineStartOffset.toFloat(), lineTop.toFloat(), endOffset.toFloat(),
            lineBottom.toFloat())
    }
}

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

    fun drawBackgroundEnd(start: Float, top: Float,
                          end: Float,
                          bottom: Float) {
        val radiiEnd = FloatArray(8)
        if (radii != null) radiiEnd.apply {
            set(0, 0f)
            set(1, 0f)
            set(2, radii[2])
            set(3, radii[3])
            set(4, radii[4])
            set(5, radii[5])
            set(6, 0f)
            set(7, 0f)
        }
        drawBackground(radiiEnd, start, top, end, bottom)
    }

    fun drawBackgroundStart(start: Float, top: Float,
                      end: Float,
                          bottom: Float) {
        val radiiStart = FloatArray(8)
        if (radii != null) {
            radiiStart.apply {
                set(0, radii[0])
                set(1, radii[1])
                set(2, 0f)
                set(3, 0f)
                set(4, 0f)
                set(5, 0f)
                set(6, radii[6])
                set(7, radii[7])
            }
        }
        drawBackground(radiiStart, start, top, end, bottom)
    }

    fun drawBackgroundMiddle(start: Float,
                         top: Float,
                         end: Float,
                         bottom: Float) {
        drawBackground(FloatArray(8), start, top, end, bottom)
    }

    fun drawBackground(start: Float,
                       top: Float,
                       end: Float,
                       bottom: Float) {
        drawBackground(radii, start, top, end, bottom)
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
