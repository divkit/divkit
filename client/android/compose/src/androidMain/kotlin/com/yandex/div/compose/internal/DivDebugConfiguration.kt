package com.yandex.div.compose.internal

import coil3.compose.AsyncImagePainter
import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
public class DivDebugConfiguration(

    /**
     * Overrides the main coroutine scope for debugging and testing.
     */
    public val coroutineScope: CoroutineScope? = null,

    /**
     * Overrides the default [kotlinx.coroutines.Dispatchers.IO].
     */
    public val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,

    /**
     * Listens for all [AsyncImagePainter]s in [com.yandex.div.compose.DivView] state changes.
     */
    public val imagePainterStateListener: ((AsyncImagePainter.State) -> Unit)? = null,

    /**
     * [TimeSource] that is used for time tracking in [com.yandex.div.compose.DivView] components.
     *
     * Provide custom value (e.g. TestCoroutineScheduler.timeSource) to mock the time source in
     * tests.
     */
    public val timeSource: TimeSource = TimeSource.Monotonic,
) {
    public companion object {
        public val Default: DivDebugConfiguration = DivDebugConfiguration()
    }
}
