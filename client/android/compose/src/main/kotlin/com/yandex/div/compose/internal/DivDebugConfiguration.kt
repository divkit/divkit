package com.yandex.div.compose.internal

import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * Provides debug configuration for [com.yandex.div.compose.DivContext].
 *
 * This class is intended for debugging and testing only. Do not use it in the production
 * environment.
 *
 * @see com.yandex.div.compose.DivContext
 */
@InternalApi
@OptIn(ExperimentalTime::class)
class DivDebugConfiguration(
    val coroutineScope: CoroutineScope? = null,
    val timeSource: TimeSource?
) {
    constructor(
        coroutineScope: CoroutineScope? = null,
    ): this(
        coroutineScope = coroutineScope,
        timeSource = null
    )
}
