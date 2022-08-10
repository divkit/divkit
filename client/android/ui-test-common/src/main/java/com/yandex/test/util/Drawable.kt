package com.yandex.test.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.VectorDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

object Drawable {

    /**
     * Compares drawable from resource and concrete Drawable object
     * tint can be applied to drawable from resource before comparing (tint is not applied by default)
     * @param context - context where Drawable is exists
     * @param targetDrawable - Drawable object to compare
     * @param resourceId - id from resources to compare
     * @param tint (optional) - tint will be applied to drawable from resources
     * @param color (optional) - color will be applied to drawable from resources
     * @return boolean - result of compare
     */
    fun checkBitmap(
        context: Context,
        targetDrawable: Drawable?,
        resourceId: Int,
        @ColorRes tint: Int? = null,
        @ColorRes color: Int? = null
    ): Boolean {
        val drawableFromResource = context.resources.getDrawable(resourceId, null)
        val drawableCompat = AppCompatResources.getDrawable(context, resourceId)

        tint?.let {
            drawableFromResource.setTintList(ColorStateList.valueOf(ContextCompat.getColor(context, it)))
            drawableCompat?.setTintList(ColorStateList.valueOf(ContextCompat.getColor(context, it)))
            // see note in https://developer.android.com/reference/android/widget/ImageView#attr_android:tint
            drawableFromResource.setTintMode(PorterDuff.Mode.SRC_ATOP)
            drawableCompat?.setTintMode(PorterDuff.Mode.SRC_ATOP)
        }

        color?.let {
            val colorInt = ContextCompat.getColor(context, it)
            drawableFromResource.mutate().colorFilter = PorterDuffColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            drawableCompat?.mutate()?.colorFilter = PorterDuffColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
        }

        return checkBitmap(targetDrawable, drawableFromResource) || checkBitmap(targetDrawable, drawableCompat)
    }

    /**
     * Compares two drawables as bitmaps, vector drawable will be transformed to bitmap and then compared
     * @param targetDrawable - first drawable
     * @param drawableFromResource - second drawable
     * @return boolean - result of compare
     */
    fun checkBitmap(targetDrawable: Drawable?, drawableFromResource: Drawable?): Boolean {
        var drawable = targetDrawable
        var otherDrawable = drawableFromResource

        if (drawable == null || otherDrawable == null) return false

        if (drawable is VectorDrawable || drawable is VectorDrawableCompat ||
            otherDrawable is VectorDrawable || otherDrawable is VectorDrawableCompat
        ) {
            val bitmap = createBitmapFromDrawable(drawable)
            val otherBitmap = createBitmapFromDrawable(otherDrawable)

            return bitmap.sameAs(otherBitmap)
        }

        if (drawable is StateListDrawable && otherDrawable is StateListDrawable) {
            drawable = drawable.current
            otherDrawable = otherDrawable.current
        }

        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val otherBitmap = (otherDrawable as BitmapDrawable).bitmap
            return bitmap.sameAs(otherBitmap)
        }

        return false
    }

    fun getAverageBackgroundColorForViewDrawable(drawable: Drawable): Int {
        if (drawable is ColorDrawable) {
//            return drawable.color
        }
        val bitmap = createBitmapFromViewDrawable(drawable)
        val pixels = IntArray(bitmap.width * bitmap.height);
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var a = 0L
        var r = 0L
        var g = 0L
        var b = 0L
        for (color in pixels) {
            a += color shr 24 and 0xFF
            r += color shr 16 and 0xFF
            g += color shr 8 and 0xFF
            b += color and 0xFF
        }
        a /= pixels.size
        r /= pixels.size
        g /= pixels.size
        b /= pixels.size
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    private fun createBitmapFromDrawable(drawable: Drawable): Bitmap {
        val leftBitmapBound = 0
        val rightBitmapBound = 0
        val bitmap = Bitmap.createBitmap(
            Math.max(drawable.intrinsicWidth, 2),
            Math.max(drawable.intrinsicHeight, 2), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        drawable.setBounds(leftBitmapBound, rightBitmapBound, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun createBitmapFromViewDrawable(drawable: Drawable): Bitmap {
        val leftBitmapBound = 0
        val rightBitmapBound = 0
        val bitmap = Bitmap.createBitmap(
            Math.max(drawable.intrinsicWidth, (drawable.callback as? View)?.width ?: 2),
            Math.max(drawable.intrinsicHeight, (drawable.callback as? View)?.height ?: 2),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        drawable.setBounds(leftBitmapBound, rightBitmapBound, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
