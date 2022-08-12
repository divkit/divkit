package com.yandex.div.core.view2.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.core.graphics.withSave
import kotlin.math.roundToInt

class ScaleDrawable(
    private val child: Drawable,
    private val scaleX: Float,
    private val scaleY: Float,
) : Drawable(), Animatable {
    constructor(child: Drawable, scale: Float) : this(child, scale, scale)

    override fun draw(canvas: Canvas) {
        canvas.withSave {
            scale(scaleX, scaleY)
            child.draw(canvas)
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