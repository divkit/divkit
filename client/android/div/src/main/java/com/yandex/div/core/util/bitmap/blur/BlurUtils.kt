package com.yandex.div.core.util.bitmap.blur

import android.graphics.Bitmap

internal object BlurUtils {
    fun isBlurParamsValid(bitmap: Bitmap, radius: Float): Boolean {
        return !bitmap.isRecycled && bitmap.width > 0 && bitmap.height > 0 && radius > 0
    }
}
