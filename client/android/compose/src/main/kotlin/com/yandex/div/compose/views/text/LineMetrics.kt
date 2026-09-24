package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.font.LineMetrics

@Composable
internal fun getLineMetrics(
    textStyle: TextStyle,
    isRequired: Boolean,
): LineMetrics? {
    if (!isRequired) {
        return null
    }

    return divContext.component.lineMetricsCache.getOrCreate(
        fontFamilyResolver = LocalFontFamilyResolver.current,
        density = LocalDensity.current,
        style = textStyle,
    )
}
