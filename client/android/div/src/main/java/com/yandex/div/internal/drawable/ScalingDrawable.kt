package com.yandex.div.internal.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.Rect
import android.graphics.drawable.Drawable

/**
 * Drawable with different scale types anf alignments. Now you can use not only center_crop, but top_crop and others.
 * Can contain either bitmap (for raster images) or picture (for vector images).
 * Examples: Fill with alignment center like center_crop in android. Fit left and top - like fit_start in android.
 */
internal class ScalingDrawable : Drawable() {

    enum class AlignmentHorizontal {
        LEFT, CENTER, RIGHT
    }

    enum class AlignmentVertical {
        TOP, CENTER, BOTTOM
    }

    enum class ScaleType {
        NO_SCALE, FIT, FILL, STRETCH
    }

    var customScaleType = ScaleType.NO_SCALE
    var alignmentHorizontal = AlignmentHorizontal.LEFT
    var alignmentVertical = AlignmentVertical.TOP
    private var originalBitmap: Bitmap? = null
    private var originalPicture: Picture? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private var thumbTransformMatrix: Matrix = Matrix()
    private var isDirtyRect = false
    private var xScale = 1f
    private var yScale = 1f
    private var xTranslate = 0f
    private var yTranslate = 0f

    override fun setAlpha(value: Int) {
        paint.alpha = value
        invalidateSelf()
    }

    override fun getOpacity(): Int = paint.alpha

    fun setBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap
        originalPicture = null
        reset()
    }

    fun setPicture(picture: Picture) {
        originalPicture = picture
        originalBitmap = null
        reset()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        reset()
    }

    private fun reset() {
        isDirtyRect = true
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun draw(canvas: Canvas) {
        canvas.save()

        // pre drawing
        val drawableWidth = originalBitmap?.width ?: originalPicture?.width ?: 0
        val drawableHeight = originalBitmap?.height ?: originalPicture?.height ?: 0

        if (drawableHeight <= 0 || drawableWidth <= 0) {
            originalBitmap?.let {
                canvas.drawBitmap(it, thumbTransformMatrix, paint)
            }
            originalPicture?.let {
                canvas.drawPicture(it)
            }
            canvas.restore()
            return
        }

        // scaling
        if (isDirtyRect) {
            val viewWidth = bounds.width().toFloat()
            val viewHeight = bounds.height().toFloat()
            xScale = viewWidth / drawableWidth
            yScale = viewHeight / drawableHeight
            when (customScaleType) {
                ScaleType.FILL -> {
                    xScale = xScale.coerceAtLeast(yScale)
                    yScale = xScale
                }
                ScaleType.FIT -> {
                    xScale = xScale.coerceAtMost(yScale)
                    yScale = xScale
                }
                ScaleType.NO_SCALE -> {
                    xScale = 1f
                    yScale = 1f
                }
                else -> Unit
            }

            // translate
            val newWidth = drawableWidth * xScale
            val newHeight = drawableHeight * yScale
            xTranslate = when (alignmentHorizontal) {
                AlignmentHorizontal.CENTER -> (viewWidth - newWidth) / 2 / xScale
                AlignmentHorizontal.RIGHT -> (viewWidth - newWidth) / xScale
                else -> 0f
            }

            yTranslate = when (alignmentVertical) {
                AlignmentVertical.CENTER -> (viewHeight - newHeight) / 2 / yScale
                AlignmentVertical.BOTTOM -> (viewHeight - newHeight) / yScale
                else -> 0f
            }
            isDirtyRect = false
        }
        canvas.scale(xScale, yScale)
        canvas.translate(xTranslate , yTranslate)

        // draw original image
        originalBitmap?.let {
            canvas.drawBitmap(it, thumbTransformMatrix, paint)
        }
        originalPicture?.let {
            canvas.drawPicture(it)
        }
        // restore frame and other
        canvas.restore()
    }
}
