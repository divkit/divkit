package com.yandex.div.compose.internal

import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.CoroutineScope

/**
 * Provides debug configuration for [com.yandex.div.compose.DivContext].
 *
 * This class is intended for debugging and testing only. Do not use it in the production
 * environment.
 *
 * @see com.yandex.div.compose.DivContext
 */
@InternalApi
class DivDebugConfiguration(
    val coroutineScope: CoroutineScope? = null,
    val imageLoaderProvider: ImageLoaderProvider? = null
)
