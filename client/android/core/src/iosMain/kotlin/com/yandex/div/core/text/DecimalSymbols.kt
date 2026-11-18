@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.core.text

import com.yandex.div.core.annotations.InternalApi
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

@InternalApi
public actual class DecimalSymbols actual constructor(languageTag: String?) {

    internal val locale: NSLocale = if (languageTag == null) {
        NSLocale.currentLocale
    } else {
        NSLocale(localeIdentifier = languageTag)
    }
}
