package com.yandex.div.core.view2.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import androidx.core.graphics.withClip

internal class TintablePictureDrawable(picture: Picture) : PictureDrawable(picture) {

    private val layerPaint = Paint()

    override fun draw(canvas: Canvas) {
        if (layerPaint.colorFilter == null) return super.draw(canvas)

        canvas.withClip(bounds) {
            // saveLayer to draw into a GPU offscreen layer, composited through layerPaint on restore
            val checkpoint = saveLayer(
                bounds.left.toFloat(),
                bounds.top.toFloat(),
                bounds.right.toFloat(),
                bounds.bottom.toFloat(),
                layerPaint,
            )
            super.draw(this)
            restoreToCount(checkpoint)
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        if (layerPaint.colorFilter == colorFilter) return
        layerPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getColorFilter(): ColorFilter? = layerPaint.colorFilter
}
