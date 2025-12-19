package com.yandex.div.core.util.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.supportFontVariations
import com.yandex.div.core.view2.divs.widgets.widthPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivTextAlignmentVertical
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import kotlin.math.max
import kotlin.math.min

internal abstract class DivTextRangesBackgroundRenderer() {

    private val tempPaint = TextPaint()

    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        span: DivBackgroundSpan,
    )

    protected fun getLineBounds(
        layout: Layout,
        line: Int,
        span: DivBackgroundSpan,
        isFirstLine: Boolean = false,
    ): LineBounds {
        val baseline = layout.getLineBaseline(line)
        val lineAscent = layout.getLineAscent(line)
        val lineDescent = layout.getLineDescent(line)

        val textAscent: Int
        val textDescent: Int
        if (span.fontSize != null) {
            tempPaint.textSize = span.fontSize.toFloat()
            tempPaint.typeface = span.typeface
            tempPaint.fontFeatureSettings = span.fontFeatureSettings
            if (supportFontVariations) {
                tempPaint.fontVariationSettings = span.fontVariationSettings
            }

            val fontMetrics = tempPaint.fontMetricsInt
            textAscent = fontMetrics.ascent
            textDescent = fontMetrics.descent
        } else {
            textAscent = lineAscent
            textDescent = lineDescent
        }

        val backgroundAscent: Int
        val backgroundDescent: Int
        if (span.lineHeight != null) {
            val textHeight = textDescent - textAscent
            val extraSpace = span.lineHeight - textHeight
            val extraTop = extraSpace / 2
            val extraBottom = extraSpace - extraTop
            backgroundAscent = textAscent - extraTop
            backgroundDescent = textDescent + extraBottom
        } else {
            backgroundAscent = textAscent
            backgroundDescent = textDescent
        }

        val baselineShift = when {
            span.baselineOffset != 0 -> -span.baselineOffset
            span.alignmentVertical == DivTextAlignmentVertical.TOP -> {
                lineAscent - textAscent
            }
            span.alignmentVertical == DivTextAlignmentVertical.CENTER -> {
                val lineCenter = (lineAscent + lineDescent) / 2
                val textCenter = (textAscent + textDescent) / 2
                lineCenter - textCenter
            }

            span.alignmentVertical == DivTextAlignmentVertical.BOTTOM -> {
                lineDescent - textDescent
            }
            else -> 0
        }

        val topOffsetShift = if (isFirstLine) -(span.topOffset ?: 0) else 0

        val top = baseline + backgroundAscent + baselineShift + topOffsetShift
        val bottom = baseline + backgroundDescent + baselineShift

        return LineBounds(top, bottom)
    }

    protected data class LineBounds(
        val top: Int,
        val bottom: Int
    )
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
        span: DivBackgroundSpan,
    ) {
        val bounds = getLineBounds(layout, startLine, span, isFirstLine = true)
        val left = min(startOffset, endOffset)
        val right = max(startOffset, endOffset)
        val borderDrawer = BackgroundDrawer(view.resources.displayMetrics, span.border, span.background, canvas, resolver)
        borderDrawer.drawBackground(left.toFloat(), bounds.top.toFloat(), right.toFloat(), bounds.bottom.toFloat())
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
        span: DivBackgroundSpan,
    ) {
        val paragDir = layout.getParagraphDirection(startLine)
        val lineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine)
        } else {
            layout.getLineRight(startLine)
        }.toInt()

        var bounds = getLineBounds(layout, startLine, span, isFirstLine = true)
        val drawer = BackgroundDrawer(view.resources.displayMetrics, span.border, span.background, canvas, resolver)
        drawer.drawBackgroundStart(startOffset.toFloat(), bounds.top.toFloat(),
            lineEndOffset.toFloat(), bounds.bottom.toFloat())

        for (line in startLine + 1 until endLine) {
            bounds = getLineBounds(layout, line, span, isFirstLine = false)
            drawer.drawBackgroundMiddle(
                (layout.getLineLeft(line).toInt()).toFloat(),
                bounds.top.toFloat(),
                (layout.getLineRight(line).toInt()).toFloat(),
                bounds.bottom.toFloat()
            )
        }

        val lineStartOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineRight(startLine)
        } else {
            layout.getLineLeft(startLine)
        }.toInt()

        bounds = getLineBounds(layout, endLine, span, isFirstLine = false)

        drawer.drawBackgroundEnd(lineStartOffset.toFloat(), bounds.top.toFloat(), endOffset.toFloat(),
            bounds.bottom.toFloat())
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
