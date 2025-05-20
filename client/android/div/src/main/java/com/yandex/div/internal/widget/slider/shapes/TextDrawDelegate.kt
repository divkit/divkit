package com.yandex.div.internal.widget.slider.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.yandex.div.core.view2.divs.supportFontVariations
import com.yandex.div.internal.widget.slider.SliderTextStyle

internal class TextDrawDelegate(private val textStyle: SliderTextStyle) {

    private val textRect = Rect()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textStyle.run {
            textSize = fontSize
            letterSpacing = spacing
            color = textColor
            typeface = fontWeight
            style = Paint.Style.FILL
            if (supportFontVariations) {
                fontVariationSettings = fontVariations
            }
        }
    }
    var text: String? = null
        set(value) {
            field = value
            textPaint.getTextBounds(value, 0, value?.length ?: 0, textRect)
            halfTextWidth = textPaint.measureText(text) / 2f
            halfTextHeight = textRect.height() / 2f
        }
    private var halfTextWidth = 0f
    private var halfTextHeight = 0f

    fun draw(canvas: Canvas, centerX: Float, centerY: Float) {
        text?.let {
            canvas.drawText(
                it,
                centerX - halfTextWidth + textStyle.offsetX,
                centerY + halfTextHeight + textStyle.offsetY,
                textPaint
            )
        }
    }
}
