package com.yandex.div.compose.images

import android.net.Uri

private const val EMPTY_IMAGE_URL = "empty://"

internal fun Uri.isValidImageUri(): Boolean {
    return toString() != EMPTY_IMAGE_URL
}
