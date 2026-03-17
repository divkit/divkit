package com.yandex.div.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.PictureDrawable
import android.util.Base64
import androidx.annotation.WorkerThread
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.svg.SvgDecoder

internal class DecodeBase64ImageTask(
    private var rawBase64string: String,
    private val synchronous: Boolean,
    private val onDecoded: (ImageRepresentation?) -> Unit
) : Runnable {

    @WorkerThread
    override fun run() {
        val base64string = extractFromDataUrl(rawBase64string)
        val bytes = try {
            Base64.decode(base64string, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            KLog.e("Div") { "Bad base-64 image preview" }
            return
        }

        val decoded = if (isSvg(rawBase64string)) {
            decodeToPictureDrawable(bytes)?.asImageRepresentation()
        } else {
            decodeToBitmap(bytes)?.asImageRepresentation()
        }

        if (synchronous) {
            onDecoded(decoded)
        } else {
            UiThreadHandler.postOnMainThread {
                onDecoded(decoded)
            }
        }
    }

    private fun decodeToBitmap(bytes: ByteArray): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: IllegalArgumentException) {
            KLog.e("Div") { "Problem with decoding base-64 preview image occurred" }
            return null
        }
        return bitmap
    }

    private fun extractFromDataUrl(base64string: String): String {
        // this is data-url format: data:[;base64],<data>
        if (base64string.startsWith("data:")) {
            return base64string.substring(base64string.indexOf(',') + 1)
        }
        return base64string
    }

    private fun decodeToPictureDrawable(bytes: ByteArray): PictureDrawable? {
        return SvgDecoder.decode(bytes.inputStream())
    }

    private fun isSvg(base64string: String): Boolean {
        return base64string.startsWith("data:image/svg")
    }

    private fun Bitmap.asImageRepresentation() = ImageRepresentation.Bitmap(this)

    private fun PictureDrawable.asImageRepresentation() = ImageRepresentation.PictureDrawable(this)
}
