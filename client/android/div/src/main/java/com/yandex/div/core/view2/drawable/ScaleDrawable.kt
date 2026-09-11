package com.yandex.div.core.view2.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import androidx.core.graphics.withSave
import kotlin.math.roundToInt

internal class ScaleDrawable(
    private val child: Drawable,
    private val scaleX: Float,
    private val scaleY: Float,
) : Drawable(), Drawable.Callback, Animatable {
    constructor(child: Drawable, scale: Float) : this(child, scale, scale)

    init {
        child.callback = this
    }

    override fun onBoundsChange(bounds: Rect) {
        child.setBounds(
            (bounds.left / scaleX).roundToInt(),
            (bounds.top / scaleY).roundToInt(),
            (bounds.right / scaleX).roundToInt(),
            (bounds.bottom / scaleY).roundToInt(),
        )
    }

    override fun invalidateDrawable(who: Drawable) = invalidateSelf()

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) = scheduleSelf(what, `when`)

    override fun unscheduleDrawable(who: Drawable, what: Runnable) = unscheduleSelf(what)

    override fun draw(canvas: Canvas) {
        canvas.withSave {
            scale(scaleX, scaleY)
            if (child is PictureDrawable) {
                canvas.drawPicture(child.picture)
            } else {
                child.draw(canvas)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        child.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        child.colorFilter = colorFilter
    }

    override fun getOpacity() = child.opacity

    override fun getIntrinsicWidth(): Int {
        if (child.intrinsicWidth == -1) {
            return -1
        }
        return (child.intrinsicWidth * scaleX).roundToInt()
    }

    override fun getIntrinsicHeight(): Int {
        if (child.intrinsicHeight == -1) {
            return -1
        }
        return (child.intrinsicHeight * scaleY).roundToInt()
    }

    override fun start() {
        if (child is Animatable) child.start()
    }

    override fun stop() {
        if (child is Animatable) child.stop()
    }

    override fun isRunning() = child is Animatable && child.isRunning
}
