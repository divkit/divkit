package com.yandex.divkit.demo.div.editor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

object DivEditorScreenshot {

    fun AppCompatActivity.takeScreenshot(
        tookScreenshotCallback: (bitmap: Bitmap) -> Unit
    ) {
        val rootView: View = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.pixelCopy(bitmap, tookScreenshotCallback)
        } else {
            val canvas = Canvas(bitmap)
            rootView.draw(canvas)
            tookScreenshotCallback.invoke(bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Window.pixelCopy(bitmap: Bitmap, tookScreenshotCallback: (bitmap: Bitmap) -> Unit) {
        try {
            PixelCopy.request(this, bitmap, { copyResult: Int ->
                if (copyResult == PixelCopy.SUCCESS) {
                    tookScreenshotCallback.invoke(bitmap)
                }
            }, Handler(Looper.getMainLooper()))
        } catch (ignored: Exception) {}
    }
}
