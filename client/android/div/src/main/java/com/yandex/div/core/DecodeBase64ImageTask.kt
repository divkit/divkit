package com.yandex.div.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.annotation.WorkerThread
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.UiThreadHandler

internal class DecodeBase64ImageTask(
    private var base64string: String,
    private val synchronous: Boolean,
    private val onDecoded: (Bitmap?) -> Unit
) : Runnable {

    @WorkerThread
    override fun run() {
        base64string = extractFromDataUrl(base64string)
        val bytes = try {
            Base64.decode(base64string, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            KLog.e("Div") { "Bad base-64 image preview" }
            return
        }
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: IllegalArgumentException) {
            KLog.e("Div") { "Problem with decoding base-64 preview image occurred" }
            return
        }

        if (synchronous) {
            onDecoded(bitmap)
        } else {
            UiThreadHandler.postOnMainThread {
                onDecoded(bitmap)
            }
        }
    }

    private fun extractFromDataUrl(base64string: String): String {
        // this is data-url format: data:[;base64],<data>
        if (base64string.startsWith("data:")) {
            return base64string.substring(base64string.indexOf(',') + 1)
        }
        return base64string
    }
}
