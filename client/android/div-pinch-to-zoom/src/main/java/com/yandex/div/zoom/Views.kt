package com.yandex.div.zoom

import android.graphics.Bitmap
import android.view.View
import androidx.core.graphics.applyCanvas

internal val View.isActuallyLaidOut: Boolean
    get() = width > 0 && height > 0

internal fun View.drawToBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    if (!isActuallyLaidOut) {
        throw IllegalStateException("View needs to be laid out before calling drawToBitmap()")
    }

    return Bitmap.createBitmap(width, height, config).applyCanvas {
        translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(this)
    }
}
