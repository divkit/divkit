package com.yandex.test.screenshot

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.Surface
import android.view.SurfaceView
import android.view.Window
import androidx.annotation.RequiresApi

/**
 * Kotlin-friendly wrapper over [PixelCopy]
 */
@RequiresApi(Build.VERSION_CODES.O)
internal object PixelKopy {

    fun request(surface: Surface, srcRect: Rect? = null, dst: Bitmap, handler: Handler, callback: (Int) -> Unit) {
        PixelCopy.request(surface, srcRect, dst, callback, handler)
    }

    fun request(surface: SurfaceView, srcRect: Rect? = null, dst: Bitmap, handler: Handler, callback: (Int) -> Unit) {
        PixelCopy.request(surface, srcRect, dst, callback, handler)
    }

    fun request(window: Window, srcRect: Rect? = null, dst: Bitmap, handler: Handler, callback: (Int) -> Unit) {
        PixelCopy.request(window, srcRect, dst, callback, handler)
    }
}
