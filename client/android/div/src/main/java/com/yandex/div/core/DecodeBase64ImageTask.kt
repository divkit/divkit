package com.yandex.div.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.PictureDrawable
import android.util.Base64
import androidx.annotation.WorkerThread
import androidx.core.graphics.drawable.toBitmap
import com.yandex.div.core.svg.SvgDecoder
import com.yandex.div.core.util.ImageRepresentation

import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.UiThreadHandler

internal class DecodeBase64ImageTask(
    private var base64string: String,
    private val synchronous: Boolean,
    private val shouldBeRasterized: Boolean,
    private val onDecoded: (ImageRepresentation<Any>?) -> Unit
) : Runnable {

    private val svgDecoder = SvgDecoder()

    @WorkerThread
    override fun run() {
        val imageFormat = extractFormatFromDataUrlOrNull(base64string)
        base64string = extractDataFromDataUrl(base64string)
        val bytes = try {
            Base64.decode(base64string, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            KLog.e("Div") { "Bad base-64 image preview" }
            return
        }

        // Decode to an appropriate format if it's specified in the preview
        // Otherwise, make a guess and try decoding to both
        val decoded: ImageRepresentation<Any> = imageFormat?.let {
            if (imageFormat == "svg") {
                getAppropriateSvgRepresentation(decodeToPictureDrawable(bytes))
            } else {
                ImageRepresentation.Bitmap(decodeToBitmap(bytes))
            }
        } ?: tryDecodeToBoth(bytes)

        if (synchronous) {
            onDecoded(decoded)
        } else {
            UiThreadHandler.postOnMainThread {
                onDecoded(decoded)
            }
        }
    }

    private fun tryDecodeToBoth(bytes: ByteArray): ImageRepresentation<Any> {
        decodeToBitmap(bytes)?.let {
            return ImageRepresentation.Bitmap(it)
        } ?: return getAppropriateSvgRepresentation(decodeToPictureDrawable(bytes))
    }

    private fun getAppropriateSvgRepresentation(pictureDrawable: PictureDrawable?): ImageRepresentation<Any> {
        return if (shouldBeRasterized) {
            convertPictureDrawableToBitmap(pictureDrawable)
        } else {
            ImageRepresentation.PictureDrawable(pictureDrawable)
        }
    }

    private fun convertPictureDrawableToBitmap(pictureDrawable: PictureDrawable?): ImageRepresentation<Bitmap> {
        val bitmap = pictureDrawable?.toBitmap(pictureDrawable.intrinsicWidth, pictureDrawable.intrinsicHeight)
        return ImageRepresentation.Bitmap(bitmap)
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

    private fun decodeToPictureDrawable(bytes: ByteArray): PictureDrawable? {
        return svgDecoder.decode(bytes.inputStream())
    }

    private fun extractFormatFromDataUrlOrNull(base64string: String) : String? {
        if (containsData(base64string)) {
            return base64string.substring(base64string.indexOf('/') + 1, base64string.indexOf(';'))
        }
        return null
    }

    private fun extractDataFromDataUrl(base64string: String): String {
        if (containsData(base64string)) {
            return base64string.substring(base64string.indexOf(',') + 1)
        }
        return base64string
    }

    private fun containsData(base64string: String): Boolean {
        // this is data-url format: data:[;base64],<data>
        return base64string.startsWith("data:")
    }
}
