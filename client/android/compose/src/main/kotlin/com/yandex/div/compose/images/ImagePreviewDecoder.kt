package com.yandex.div.compose.images

import android.util.Base64
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

private const val PREVIEW_IS_NOT_BASE64_IMAGE = "Preview doesn't contain base64 image"

@DivContextScope
internal class ImagePreviewDecoder @Inject constructor(
    private val reporter: DivReporter
) {
    fun decodePreview(data: String): ByteArray? {
        return try {
            Base64.decode(
                if (data.startsWith("data:")) {
                    data.substring(data.indexOf(',') + 1)
                } else {
                    data
                },
                Base64.DEFAULT
            )
        } catch (e: IllegalArgumentException) {
            reporter.reportError(IllegalArgumentException(PREVIEW_IS_NOT_BASE64_IMAGE, e))
            null
        }
    }
}
