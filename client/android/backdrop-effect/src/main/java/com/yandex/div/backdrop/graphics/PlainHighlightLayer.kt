package com.yandex.div.backdrop.graphics

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.PaintCompat
import com.yandex.div.backdrop.util.alphaByte
import com.yandex.div.backdrop.util.multiplyAlphaBytes
import kotlin.math.ceil
import kotlin.math.min

internal class PlainHighlightLayer : HighlightLayer() {

    private var width: Float = 0.0f
    private var height: Float = 0.0f

    @ColorInt
    private var highlightColor: Int = Color.WHITE
    private var highlightAlpha: Int = 255
    private var highlightRimWidth: Float = 0.0f
    private var highlightBlurRadius: Float = 0.0f

    private var invalidated = true

    private val outlineCornerRadii: FloatArray = FloatArray(8)
    private val highlightOutline = Path()

    private val highlightPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        PaintCompat.setBlendMode(this, BlendModeCompat.PLUS)
    }

    override fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height

        invalidated = true
    }

    override fun setCornerRadius(radius: Float) {
        outlineCornerRadii.fill(radius)

        invalidated = true
    }

    override fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) {
        outlineCornerRadii[CORNER_TOP_LEFT_X] = topLeft
        outlineCornerRadii[CORNER_TOP_LEFT_Y] = topLeft
        outlineCornerRadii[CORNER_TOP_RIGHT_X] = topRight
        outlineCornerRadii[CORNER_TOP_RIGHT_Y] = topRight
        outlineCornerRadii[CORNER_BOTTOM_RIGHT_X] = bottomRight
        outlineCornerRadii[CORNER_BOTTOM_RIGHT_Y] = bottomRight
        outlineCornerRadii[CORNER_BOTTOM_LEFT_X] = bottomLeft
        outlineCornerRadii[CORNER_BOTTOM_LEFT_Y] = bottomLeft

        invalidated = true
    }

    override fun setHighlightEffect(rimWidth: Float, alpha: Float, color: Int, angle: Float, falloff: Float) {
        if (rimWidth <= 0.0f) {
            resetHighlightEffect()
            return
        }

        highlightColor = color
        highlightAlpha = alphaByte(alpha)
        highlightRimWidth = rimWidth
        highlightBlurRadius = rimWidth / 2.0f

        invalidated = true
    }

    override fun setHighlightEffect(rimWidth: Float, alpha: Float, intensity: Float, angle: Float) {
        if (rimWidth <= 0.0f) {
            resetHighlightEffect()
            return
        }

        highlightColor = ColorUtils.setAlphaComponent(Color.WHITE, alphaByte(intensity))
        highlightAlpha = alphaByte(alpha)
        highlightRimWidth = rimWidth
        highlightBlurRadius = rimWidth / 2.0f

        invalidated = true
    }

    private fun resetHighlightEffect() {
        highlightColor = Color.WHITE
        highlightAlpha = 255
        highlightRimWidth = 0.0f
        highlightBlurRadius = 0.0f

        highlightPaint.apply {
            color = Color.WHITE
            strokeWidth = 0.0f
            maskFilter = null
            shader = null
        }

        invalidated = true
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        if (width <= 0.0f || height <= 0.0f || highlightRimWidth <= 0.0f) return

        val maxRimWidth = min(width, height) / 2.0f

        if (invalidated) {
            highlightOutline.rewind()
            highlightOutline.addRoundRect(0.0f, 0.0f, width, height, outlineCornerRadii, Path.Direction.CW)

            highlightPaint.color = highlightColor
            highlightPaint.strokeWidth = ceil(min(highlightRimWidth, maxRimWidth)) * 2.0f
            highlightPaint.maskFilter = if (highlightBlurRadius > 0.0f) {
                BlurMaskFilter(highlightBlurRadius, BlurMaskFilter.Blur.NORMAL)
            } else {
                null
            }

            invalidated = false
        }

        val alpha = multiplyAlphaBytes(highlightAlpha, paint.alpha)
        val count = canvas.saveLayerAlpha(0.0f, 0.0f, width, height, alpha)
        canvas.clipPath(highlightOutline)
        canvas.drawPath(highlightOutline, highlightPaint)
        canvas.restoreToCount(count)
    }

    private companion object {
        const val CORNER_TOP_LEFT_X = 0
        const val CORNER_TOP_LEFT_Y = 1
        const val CORNER_TOP_RIGHT_X = 2
        const val CORNER_TOP_RIGHT_Y = 3
        const val CORNER_BOTTOM_RIGHT_X = 4
        const val CORNER_BOTTOM_RIGHT_Y = 5
        const val CORNER_BOTTOM_LEFT_X = 6
        const val CORNER_BOTTOM_LEFT_Y = 7
    }
}
