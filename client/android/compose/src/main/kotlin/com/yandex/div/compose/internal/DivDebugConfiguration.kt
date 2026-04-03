package com.yandex.div.compose.internal

import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.CoroutineScope

/**
 * Provides debug components for [com.yandex.div.compose.DivComposeConfiguration].
 *
 * This class is intended for debugging and testing only. Do not use it in the production
 * environment.
 */
@InternalApi
class DivDebugConfiguration(
    val coroutineScope: CoroutineScope? = null,
    val imageLoaderProvider: ImageLoaderProvider? = null
)
