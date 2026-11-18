package com.yandex.div.core.text

import com.yandex.div.core.annotations.InternalApi
import java.text.DecimalFormatSymbols
import java.util.Locale

@InternalApi
public actual class DecimalSymbols actual constructor(languageTag: String?) {

    internal val value: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(
        if (languageTag == null) Locale.getDefault() else Locale.forLanguageTag(languageTag)
    )
}
