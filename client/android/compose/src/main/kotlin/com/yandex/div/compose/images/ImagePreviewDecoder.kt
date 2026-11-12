package com.yandex.div.compose.images

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject
import kotlin.io.encoding.Base64

@DivContextScope
internal class ImagePreviewDecoder @Inject constructor(
    private val reporter: DivReporter
) {
    private val base64 = Base64.Mime.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)

    fun decodePreview(data: String): ByteArray? {
        return try {
            base64.decode(
                if (data.startsWith("data:")) {
                    data.substring(data.indexOf(',') + 1)
                } else {
                    data
                }
            )
        } catch (e: IllegalArgumentException) {
            reporter.reportError(
                IllegalArgumentException("Failed to decode Base64 image preview", e)
            )
            null
        }
    }
}
