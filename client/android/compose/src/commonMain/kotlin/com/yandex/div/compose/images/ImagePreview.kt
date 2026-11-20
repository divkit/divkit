package com.yandex.div.compose.images

import coil3.Uri

internal sealed interface ImagePreview {
    data class Url(val uri: Uri) : ImagePreview
    data class Base64(val bytes: ByteArray) : ImagePreview
}
