package com.yandex.div.internal.spannable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.yandex.div.internal.Assert
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * ImageSpan with symbol replacement
 */
internal class BitmapImageSpan @JvmOverloads constructor(
    context: Context,
    bitmap: Bitmap,
    private val lineHeight: Int = 0,
    private val fontSize: Int = 0,
    width: Int,
    height: Int,
    @ColorInt tintColor: Int? = null,
    tintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN,
    isSquare: Boolean = true,
    internal val accessibilityDescription: String?,
    internal val accessibilityType: String,
    internal val onClickAccessibilityAction: OnAccessibilityClickAction?,
    internal val anchorPoint: AnchorPoint = AnchorPoint.LINE_BOTTOM
) : PositionAwareReplacementSpan() {

    enum class AnchorPoint {
        BASELINE,
        LINE_BOTTOM
    }

    private val drawable: Drawable

    internal var drawnTop = 0f
    internal var drawnBottom = 0f
    internal var drawnLeft = 0f
    internal var drawnRight = 0f

    init {
        drawable = BitmapDrawable(context.resources, bitmap)
        if (isSquare) {
            scaleDrawableWithAspectRatio(bitmap, width, height)
        } else {
            drawable.setBounds(0, 0, width, height)
        }
        if (tintColor != null) {
            drawable.colorFilter = PorterDuffColorFilter(tintColor, tintMode)
        }
    }

    private fun scaleDrawableWithAspectRatio(bitmap: Bitmap, width: Int, height: Int) {
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val widthScaleFactor = if (width > 0) bitmapWidth / width.toFloat() else 1f
        val heightScaleFactor = if (height > 0) bitmapHeight / height.toFloat() else 1f
        val scaleFactor = max(widthScaleFactor, heightScaleFactor)
        drawable.setBounds(
            0, 0,
            if (bitmapWidth > 0 && scaleFactor != 0f) (bitmapWidth / scaleFactor).toInt() else 0,
            if (bitmapHeight > 0 && scaleFactor != 0f) (bitmapHeight / scaleFactor).toInt() else 0
        )
    }

    override fun adjustSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm != null && lineHeight <= 0) {
            // The code in this file is not ready for non-zero drawable top, consider revising
            Assert.assertEquals(drawable.bounds.top, 0)

            val imageHeight = drawable.bounds.height()
            val imageOffset = getImageOffset(imageHeight, paint).roundToInt()
            val imageBaseline = when (anchorPoint) {
                AnchorPoint.BASELINE -> 0
                AnchorPoint.LINE_BOTTOM -> fm.bottom
            }

            val targetAscent = -imageHeight + imageOffset + imageBaseline
            val targetDescent = targetAscent + imageHeight
            val topAscent = fm.top - fm.ascent
            val bottomDescent = fm.bottom - fm.descent

            fm.ascent = min(targetAscent, fm.ascent)
            fm.descent = max(targetDescent, fm.descent)
            fm.top = fm.ascent + topAscent
            fm.bottom = fm.descent + bottomDescent
        }
        return drawable.bounds.right
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        start: Int, end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        canvas.save()
        val anchor = when (anchorPoint) {
            AnchorPoint.LINE_BOTTOM -> bottom
            AnchorPoint.BASELINE -> y
        }
        val offset = getImageOffset(drawable.bounds.height(), paint)
        val translationY = anchor - drawable.bounds.bottom + offset

        drawnBottom = translationY + drawable.bounds.bottom + offset
        drawnTop = translationY + offset
        drawnLeft = x
        drawnRight = x + drawable.bounds.right
        canvas.translate(x, translationY)
        drawable.draw(canvas)
        canvas.restore()
    }

    private fun getImageOffset(imageHeight: Int, paint: Paint): Float {
        val textScale = if (fontSize > 0) fontSize / paint.textSize else 1.0f
        val textCenter = (paint.ascent() + paint.descent()) / 2.0f * textScale
        val imageCenter = -imageHeight.toFloat() / 2.0f
        return textCenter - imageCenter
    }

    internal fun interface OnAccessibilityClickAction {
        fun perform()
    }
}
