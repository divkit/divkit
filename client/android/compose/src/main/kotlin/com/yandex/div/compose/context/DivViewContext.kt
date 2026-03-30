package com.yandex.div.compose.context

import androidx.compose.runtime.compositionLocalOf
import com.yandex.div.compose.DivException

internal class DivViewContext(
    val rootLocalContext: DivLocalContext
) {
    /**
     * Stores [DivLocalContext]s for view elements.
     */
    val localContextStorage = DivLocalContextStorage()
}

internal val LocalDivViewContext = compositionLocalOf<DivViewContext> {
    throw DivException("DivViewContext not provided")
}
