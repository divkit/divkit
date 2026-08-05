package com.yandex.div.backdrop.graphics

import android.graphics.BlendMode
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import com.yandex.div.backdrop.util.alphaByte
import com.yandex.div.backdrop.util.multiplyAlphaBytes
import kotlin.math.ceil
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class ReflectionRimHighlightLayer : RimHighlightLayer() {

    private var width: Float = 0.0f
    private var height: Float = 0.0f

    @ColorInt
    private var highlightColor: Int = Color.WHITE
    private var highlightAlpha: Int = 255
    private var highlightWidth: Float = 0.0f
    private var highlightBlurRadius: Float = 0.0f
    private var highlightAngle: Float = 0.0f
    private var highlightFalloff: Float = 0.0f
    private var useAmbientHighlight = false

    private var invalidated = true

    private val shaderCornerRadii: FloatArray = FloatArray(4)
    private val outlineCornerRadii: FloatArray = FloatArray(8)
    private val highlightOutline = Path()

    private val highlightPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        blendMode = BlendMode.PLUS
    }

    override fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height

        invalidated = true
    }

    override fun setCornerRadius(radius: Float) {
        shaderCornerRadii.fill(radius)
        outlineCornerRadii.fill(radius)

        invalidated = true
    }

    override fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) {
        shaderCornerRadii[CORNER_TOP_LEFT] = topLeft
        shaderCornerRadii[CORNER_TOP_RIGHT] = topRight
        shaderCornerRadii[CORNER_BOTTOM_RIGHT] = bottomRight
        shaderCornerRadii[CORNER_BOTTOM_LEFT] = bottomLeft

        outlineCornerRadii[CORNER_TOP_LEFT.cornerX()] = topLeft
        outlineCornerRadii[CORNER_TOP_LEFT.cornerY()] = topLeft
        outlineCornerRadii[CORNER_TOP_RIGHT.cornerX()] = topRight
        outlineCornerRadii[CORNER_TOP_RIGHT.cornerY()] = topRight
        outlineCornerRadii[CORNER_BOTTOM_RIGHT.cornerX()] = bottomRight
        outlineCornerRadii[CORNER_BOTTOM_RIGHT.cornerY()] = bottomRight
        outlineCornerRadii[CORNER_BOTTOM_LEFT.cornerX()] = bottomLeft
        outlineCornerRadii[CORNER_BOTTOM_LEFT.cornerY()] = bottomLeft

        invalidated = true
    }

    override fun setRimHighlightEffect(width: Float, alpha: Float, color: Int, angle: Float, falloff: Float) {
        if (width <= 0.0f) {
            resetHighlightEffect()
            return
        }

        highlightColor = color
        highlightAlpha = alphaByte(alpha)
        highlightWidth = width
        highlightBlurRadius = width / 2.0f
        highlightAngle = angle
        highlightFalloff = falloff
        useAmbientHighlight = false

        invalidated = true
    }

    override fun setRimHighlightEffect(width: Float, alpha: Float, intensity: Float, angle: Float) {
        if (width <= 0.0f) {
            resetHighlightEffect()
            return
        }

        highlightColor = ColorUtils.setAlphaComponent(Color.WHITE, alphaByte(intensity))
        highlightAlpha = alphaByte(alpha)
        highlightWidth = width
        highlightBlurRadius = width / 2.0f
        highlightAngle = angle
        highlightFalloff = 0.0f
        useAmbientHighlight = true

        invalidated = true
    }

    private fun resetHighlightEffect() {
        highlightColor = Color.WHITE
        highlightAlpha = 255
        highlightWidth = 0.0f
        highlightBlurRadius = 0.0f
        highlightAngle = 0.0f
        highlightFalloff = 0.0f
        useAmbientHighlight = false

        highlightPaint.apply {
            color = Color.WHITE
            strokeWidth = 0.0f
            maskFilter = null
            shader = null
        }

        invalidated = true
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        if (width <= 0.0f || height <= 0.0f || highlightWidth <= 0.0f) return

        val maxWidth = min(width, height) / 2.0f

        if (invalidated) {
            highlightOutline.rewind()
            highlightOutline.addRoundRect(0.0f, 0.0f, width, height, outlineCornerRadii, Path.Direction.CW)

            highlightPaint.color = highlightColor
            highlightPaint.strokeWidth = ceil(min(highlightWidth, maxWidth)) * 2.0f
            highlightPaint.maskFilter = if (highlightBlurRadius > 0.0f) {
                BlurMaskFilter(highlightBlurRadius, BlurMaskFilter.Blur.NORMAL)
            } else {
                null
            }

            invalidated = false
        }

        highlightPaint.shader = if (useAmbientHighlight) {
            Shaders.ambientHighlight(
                width = width,
                height = height,
                cornerRadii = shaderCornerRadii.clone(),
                angle = highlightAngle,
            )
        } else {
            Shaders.specularHighlight(
                width = width,
                height = height,
                cornerRadii = shaderCornerRadii.clone(),
                color = highlightColor,
                angle = highlightAngle,
                falloff = highlightFalloff,
            )
        }

        val alpha = multiplyAlphaBytes(highlightAlpha, paint.alpha)
        val count = canvas.saveLayerAlpha(0.0f, 0.0f, width, height, alpha)
        canvas.clipPath(highlightOutline)
        canvas.drawPath(highlightOutline, highlightPaint)
        canvas.restoreToCount(count)
    }

    private companion object {
        const val CORNER_TOP_LEFT = 0
        const val CORNER_TOP_RIGHT = 1
        const val CORNER_BOTTOM_RIGHT = 2
        const val CORNER_BOTTOM_LEFT = 3

        fun Int.cornerX() = this * 2
        fun Int.cornerY() = this * 2 + 1
    }
}
