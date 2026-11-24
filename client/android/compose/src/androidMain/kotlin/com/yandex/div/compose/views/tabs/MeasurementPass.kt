package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * True inside content that is composed only to compute its size and is never placed,
 * such as the page measurement copy in [DivTabsView]. The displayed copy of the same
 * content shares the [com.yandex.div.compose.di.DivViewComponent], so measured content
 * must not register card-wide targets, run actions or report events.
 */
internal val LocalIsMeasurementPass = staticCompositionLocalOf { false }
