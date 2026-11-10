package com.yandex.div.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The [DivContext] shared by [DivView]s in this composition.
 *
 * Provide it with `CompositionLocalProvider(LocalDivContext provides divContext)`.
 * [DivViewHost] provides it automatically, keeping the platform context unchanged.
 */
val LocalDivContext: ProvidableCompositionLocal<DivContext> = staticCompositionLocalOf {
    error("LocalDivContext not provided. Use CompositionLocalProvider or DivViewHost.")
}
