package com.yandex.div.internal.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable

/**
 * Drawable with different scale types anf alignments. Now you can use not only center_crop, but top_crop and others.
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
        NO_SCALE, FIT, FILL
    }

    var customScaleType = ScaleType.NO_SCALE
    var alignmentHorizontal = AlignmentHorizontal.LEFT
    var alignmentVertical = AlignmentVertical.TOP
    var originalBitmap: Bitmap? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private var thumbTransformMatrix: Matrix = Matrix()
    private var isDirtyRect = false
    private var scale = 1f
    private var xTranslate = 0f
    private var yTranslate = 0f

    override fun setAlpha(value: Int) {
        paint.alpha = value
        invalidateSelf()
    }

    override fun getOpacity(): Int = paint.alpha

    fun setBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap
        isDirtyRect = true
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        isDirtyRect = true
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun draw(canvas: Canvas) {
        canvas.save()

        // pre drawing
        val drawableWidth = originalBitmap?.width ?: 0
        val drawableHeight = originalBitmap?.height ?: 0

        if (drawableHeight <= 0 || drawableWidth <= 0) {
            originalBitmap?.let {
                canvas.drawBitmap(it, thumbTransformMatrix, paint)
            }
            canvas.restore()
            return
        }

        // scaling
        if (isDirtyRect) {
            val viewWidth = bounds.width().toFloat()
            val viewHeight = bounds.height().toFloat()
            val horizontalScale = viewWidth / drawableWidth
            val verticalScale = viewHeight / drawableHeight
            scale = when (customScaleType) {
                ScaleType.FILL -> Math.max(horizontalScale, verticalScale)
                ScaleType.FIT -> Math.min(horizontalScale, verticalScale)
                else -> 1f
            }

            // translate
            val newWidth = drawableWidth * scale
            val newHeight = drawableHeight * scale
            xTranslate = when (alignmentHorizontal) {
                AlignmentHorizontal.CENTER -> (viewWidth - newWidth) / 2 / scale
                AlignmentHorizontal.RIGHT -> (viewWidth - newWidth) / scale
                else -> 0f
            }

            yTranslate = when (alignmentVertical) {
                AlignmentVertical.CENTER -> (viewHeight - newHeight) / 2 / scale
                AlignmentVertical.BOTTOM -> (viewHeight - newHeight) / scale
                else -> 0f
            }
            isDirtyRect = false
        }
        canvas.scale(scale, scale)
        canvas.translate(xTranslate , yTranslate)

        // draw original image
        originalBitmap?.let {
            canvas.drawBitmap(it, thumbTransformMatrix, paint)
        }
        // restore frame and other
        canvas.restore()
    }
}
