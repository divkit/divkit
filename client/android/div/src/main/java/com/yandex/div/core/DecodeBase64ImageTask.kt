package com.yandex.div.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.PictureDrawable
import android.util.Base64
import androidx.annotation.WorkerThread
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.svg.SvgDecoder

internal class DecodeBase64ImageTask(
    private var rawBase64string: String,
    private val synchronous: Boolean,
    private val onDecoded: (ImageRepresentation) -> Unit
) : Runnable {

    @WorkerThread
    override fun run() {
        val base64string = extractFromDataUrl(rawBase64string)

        val result = try {
            val bytes = Base64.decode(base64string, Base64.DEFAULT)
            if (isSvg(rawBase64string)) {
                decodeToPictureDrawable(bytes).asImageRepresentation()
            } else {
                decodeToBitmap(bytes).asImageRepresentation()
            }
        } catch (e: Exception) {
            e.asImageRepresentation()
        }

        if (synchronous) {
            onDecoded(result)
        } else {
            UiThreadHandler.postOnMainThread {
                onDecoded(result)
            }
        }
    }

    @Throws
    private fun decodeToBitmap(bytes: ByteArray): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        return bitmap
    }

    private fun extractFromDataUrl(base64string: String): String {
        // this is data-url format: data:[;base64],<data>
        if (base64string.startsWith("data:")) {
            return base64string.substring(base64string.indexOf(',') + 1)
        }
        return base64string
    }

    @Throws
    private fun decodeToPictureDrawable(bytes: ByteArray) = SvgDecoder.decode(bytes.inputStream())

    private fun isSvg(base64string: String): Boolean {
        return base64string.startsWith("data:image/svg")
    }

    private fun Bitmap.asImageRepresentation() = ImageRepresentation.Bitmap(this)

    private fun PictureDrawable.asImageRepresentation() = ImageRepresentation.PictureDrawable(this)

    private fun Exception.asImageRepresentation() = ImageRepresentation.Error(this)
}
