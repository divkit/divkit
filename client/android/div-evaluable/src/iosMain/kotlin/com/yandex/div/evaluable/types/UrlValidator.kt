package com.yandex.div.evaluable.types

import com.yandex.div.core.type.Url as CoreUrl

internal actual fun isValidUrl(urlString: String): Boolean =
    try {
        CoreUrl.create(urlString)
        true
    } catch (_: IllegalArgumentException) {
        false
    }
