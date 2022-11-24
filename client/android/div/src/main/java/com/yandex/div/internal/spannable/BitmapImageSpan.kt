package com.yandex.div.internal.spannable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import com.yandex.div.core.util.Assert
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * ImageSpan with symbol replacement
 */
class BitmapImageSpan @JvmOverloads constructor(
    context: Context,
    bitmap: Bitmap,
    private val top: Float = 0.0F,
    width: Int,
    height: Int,
    @ColorInt tintColor: Int? = null,
    tintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN,
    isSquare: Boolean = true,
    private val anchorPoint: AnchorPoint = AnchorPoint.LINE_BOTTOM
) : ReplacementSpan() {

    enum class AnchorPoint {
        BASELINE,
        LINE_BOTTOM
    }

    private val drawable: Drawable

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
        val scaleFactor = Math.max(widthScaleFactor, heightScaleFactor)
        drawable.setBounds(
            0, 0,
            if (bitmapWidth > 0 && scaleFactor != 0f) (bitmapWidth / scaleFactor).toInt() else 0,
            if (bitmapHeight > 0 && scaleFactor != 0f) (bitmapHeight / scaleFactor).toInt() else 0
        )
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm != null) {
            // Workaround for API 27 and less: for unknown reason,
            // font metrics have incorrect value if ReplacementSpan is placed
            // on first char. Assuming that text is measured from start to end,
            // it's safe to reset all metrics during first char measurement.
            if (start == 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                fm.top = 0
                fm.ascent = 0
                fm.bottom = 0
                fm.descent = 0
                fm.leading = 0
            }

            // The code in this file is not ready for non-zero drawable top, consider revising
            Assert.assertEquals(drawable.bounds.top, 0)
            val imageHeight = drawable.bounds.bottom
            val desiredFmTop = when (anchorPoint) {
                AnchorPoint.LINE_BOTTOM -> ceil(imageHeight - top - fm.bottom).toInt()
                AnchorPoint.BASELINE -> ceil(imageHeight - top).toInt()
            }
            fm.ascent = min(-desiredFmTop, fm.ascent)
            fm.top = min(-desiredFmTop, fm.top)
            val desiredFmBottom = when (anchorPoint) {
                AnchorPoint.LINE_BOTTOM -> fm.bottom
                AnchorPoint.BASELINE -> ceil(top).toInt()
            }
            fm.descent = max(desiredFmBottom, fm.descent)
            fm.bottom = max(desiredFmBottom, fm.bottom)
            fm.leading = fm.descent - fm.ascent
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
        val transY = anchor - drawable.bounds.bottom + this.top
        canvas.translate(x, transY)
        drawable.draw(canvas)
        canvas.restore()
    }
}
