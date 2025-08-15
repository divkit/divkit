package com.yandex.div.core.util.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.DisplayMetrics
import com.yandex.div.core.util.bitmap.blur.BlurHelper

internal abstract class BitmapEffectHelper : BlurHelper {
    fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val mirrorMatrix = Matrix()
        mirrorMatrix.preScale(-1f, 1f)

        val result = Bitmap.createBitmap(
            /* source = */ bitmap,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ bitmap.width,
            /* height = */ bitmap.height,
            /* m = */ mirrorMatrix,
            /* filter = */ false
        )
        result.density = DisplayMetrics.DENSITY_DEFAULT
        return result
    }
}
