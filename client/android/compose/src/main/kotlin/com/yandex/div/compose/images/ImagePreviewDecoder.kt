package com.yandex.div.compose.images

import android.net.Uri
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject
import kotlin.io.encoding.Base64

@DivContextScope
internal class ImagePreviewDecoder @Inject constructor(
    private val reporter: DivReporter
) {
    private val base64 = Base64.Mime.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)

    fun decodePreview(data: String): ImagePreview? {
        val uri = Uri.parse(data)
        if (uri.scheme != null && !uri.scheme.equals("data", ignoreCase = true)) {
            return ImagePreview.Url(uri)
        }

        return try {
            val bytes = base64.decode(
                if (data.startsWith("data:")) {
                    data.substring(data.indexOf(',') + 1)
                } else {
                    data
                }
            )
            ImagePreview.Base64(bytes)
        } catch (e: IllegalArgumentException) {
            reporter.reportError(
                IllegalArgumentException("Failed to decode Base64 image preview", e)
            )
            null
        }
    }
}
