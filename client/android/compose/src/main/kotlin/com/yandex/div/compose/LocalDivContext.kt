package com.yandex.div.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.ui.platform.LocalContext

internal val LocalDivContext: ProvidableCompositionLocal<DivContext> =
    compositionLocalWithComputedDefaultOf {
        LocalContext.currentValue as? DivContext
            ?: error("DivContext not provided. Supply it through LocalContext or use DivViewHost.")
    }
