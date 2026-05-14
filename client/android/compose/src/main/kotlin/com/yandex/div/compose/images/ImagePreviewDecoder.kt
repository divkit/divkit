package com.yandex.div.compose.images

import android.util.Base64

internal fun decodePreview(data: String): ByteArray {
    return Base64.decode(
        if (data.startsWith("data:")) {
            data.substring(data.indexOf(',') + 1)
        } else {
            data
        },
        Base64.DEFAULT
    )
}
