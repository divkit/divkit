package com.yandex.div.internal.coil

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import coil3.Image
import coil3.asDrawable
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.coil.svg.SvgImage

@InternalApi
fun Image.asDivDrawable(resources: Resources, maxBitmapSize: Int): Drawable = if (this is SvgImage) {
    // Coil's generic ImageDrawable loses the SVG's intrinsic dimensions.
    ScalablePictureDrawable(asPicture(maxBitmapSize))
} else {
    asDrawable(resources)
}

private class ScalablePictureDrawable(source: Picture) : PictureDrawable(source) {

    override fun draw(canvas: Canvas) {
        picture?.let { canvas.drawPicture(it, bounds) }
    }
}
