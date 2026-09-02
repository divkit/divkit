package com.yandex.div.compose.views.image

import android.graphics.Bitmap
import android.graphics.Matrix
import coil3.size.Size
import coil3.transform.Transformation

internal object RtlMirrorTransformation : Transformation() {

    override val cacheKey: String = "rtl_mirror"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val matrix = Matrix().apply {
            preScale(-1f, 1f)
        }
        return Bitmap.createBitmap(
            input,
            0,
            0,
            input.width,
            input.height,
            matrix,
            false
        )
    }
}
