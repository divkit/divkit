package com.yandex.div.compose.internal

import coil3.compose.AsyncImagePainter
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

    /**
     * Listens for all [AsyncImagePainter]s in [com.yandex.div.compose.DivView] state changes.
     */
    val imagePainterStateListener: ((AsyncImagePainter.State) -> Unit)? = null,

    /**
     * [TimeSource] that is used for time tracking in [com.yandex.div.compose.DivView] components.
     *
     * Provide custom value (e.g. TestCoroutineScheduler.timeSource) to mock the time source in
     * tests.
     */
    val timeSource: TimeSource? = null
) {

    companion object {
        val Default = DivDebugConfiguration()
    }
}
