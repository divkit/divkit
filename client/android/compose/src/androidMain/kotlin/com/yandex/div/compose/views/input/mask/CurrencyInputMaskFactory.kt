package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.locale.CurrencyFormatInfo
import com.yandex.div2.DivCurrencyInputMask

@Composable
internal fun DivCurrencyInputMask.rememberCurrencyInputMask(): InputMask {
    val localeTag = locale?.observedValue()
    val reporter = LocalComponent.current.reporter

    return remember(localeTag) {
        val format = CurrencyFormatInfo(localeTag)
        val finalTag = format.languageTag
        if (localeTag != null && finalTag != localeTag) {
            reporter.reportWarning("Original locale tag '$localeTag' is not equals to final one '$finalTag'")
        }
        CurrencyInputMask(format)
    }
}
