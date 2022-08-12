package com.yandex.div.core.widget.slider.shapes

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.yandex.div.core.widget.slider.SliderTextStyle
import kotlin.math.absoluteValue

class TextDrawable(private val textStyle: SliderTextStyle): Drawable() {
    private var textDrawDelegate: TextDrawDelegate = TextDrawDelegate(textStyle)
    private val rect = RectF()

    fun setText(text: String) {
        textDrawDelegate.text = text
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        rect.set(bounds)
        textDrawDelegate.draw(canvas, rect.centerX(), rect.centerY())
    }

    override fun getIntrinsicHeight(): Int {
        return (textStyle.fontSize + textStyle.offsetY.absoluteValue).toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return (rect.width() + textStyle.offsetX.absoluteValue).toInt()
    }

    fun getOffsetY() = textStyle.offsetY.toInt()

    fun getTextHeight() = textStyle.fontSize.toInt()

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    override fun getOpacity(): Int = PixelFormat.OPAQUE

}
