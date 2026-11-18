package com.yandex.div.compose.views.background

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntRect
import coil3.BitmapImage

// Isolate the pixels of each slice so bilinear filtering clamps at its edges.
internal fun BitmapImage.crop(rect: IntRect): ImageBitmap {
    if (rect.width == width && rect.height == height) {
        return bitmap.asImageBitmap()
    }
    return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width, rect.height).asImageBitmap()
}

internal fun BitmapImage.densityScale(canvas: Canvas): Float {
    val source = bitmap.density
    val target = canvas.nativeCanvas.density
    return if (source == 0 || target == 0) 1f else target.toFloat() / source
}
