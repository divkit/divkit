package com.yandex.div.core.widget.slider

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import com.yandex.div.core.widget.slider.shapes.TextDrawable

internal class SliderDrawDelegate {
    private var viewportWidth = 0
    private var viewportHeight = 0

    private val centerY: Int
        get() = viewportHeight / 2

    fun onMeasure(viewportWidth: Int, viewportHeight: Int) {
        this.viewportWidth = viewportWidth
        this.viewportHeight = viewportHeight
    }

    fun drawInactiveTrack(canvas: Canvas, drawable: Drawable?) {
        drawable ?: return
        drawable.setBounds(
            0,
            drawable.calculateTop(),
            viewportWidth,
            drawable.calculateBottom()
        )
        drawable.draw(canvas)
    }

    fun drawActiveTrack(
        canvas: Canvas,
        drawable: Drawable?,
        @Px from: Int,
        @Px to: Int
    ) {
        drawable ?: return
        drawable.setBounds(
            from,
            drawable.calculateTop(),
            to,
            drawable.calculateBottom()
        )

        drawable.draw(canvas)
    }

    fun drawThumb(canvas: Canvas, position: Int, drawable: Drawable?,
                  value: Int, textDrawable: TextDrawable?) {
        drawOnPosition(
            canvas,
            drawable,
            position
        )
        textDrawable?.let {
            it.setText(value.toString())
            drawOnPosition(canvas, it, position)
        }
    }

    fun drawOnPosition(
        canvas: Canvas,
        drawable: Drawable?,
        position: Int
    ) {
        drawable ?: return
        val halfWidth = drawable.intrinsicWidth / 2
        drawable.setBounds(
            position - halfWidth,
            drawable.calculateTop(),
            position + halfWidth,
            drawable.calculateBottom()
        )
        drawable.draw(canvas)
    }

    private fun Drawable.calculateTop() = centerY - this.intrinsicHeight / 2

    private fun Drawable.calculateBottom() = centerY + this.intrinsicHeight / 2
}
