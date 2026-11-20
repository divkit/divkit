package com.yandex.div.compose.images

import coil3.toUri
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject
import kotlin.io.encoding.Base64

@DivContextScope
@Inject
internal class ImagePreviewDecoder(
    private val reporter: DivReporter
) {
    private val base64 = Base64.Mime.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)

    fun decodePreview(data: String): ImagePreview? {
        val uri = data.toUri()
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
