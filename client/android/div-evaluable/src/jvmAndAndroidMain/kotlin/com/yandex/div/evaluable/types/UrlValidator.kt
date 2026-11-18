package com.yandex.div.evaluable.types

import java.net.MalformedURLException
import java.net.URL

// Keep URL handler lookup and validation identical for existing JVM and Android consumers.
internal actual fun isValidUrl(urlString: String): Boolean {
    return try {
        URL(urlString)
        true
    } catch (e: MalformedURLException) {
        false
    }
}
