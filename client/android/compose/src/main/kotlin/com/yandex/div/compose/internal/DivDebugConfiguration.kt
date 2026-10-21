package com.yandex.div.compose.internal

import coil3.compose.AsyncImagePainter
import com.yandex.div.compose.dagger.Names
import com.yandex.div.core.annotations.InternalApi
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
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
@Module
class DivDebugConfiguration(

    /**
     * Overrides the main coroutine scope for debugging and testing.
     */
    val coroutineScope: CoroutineScope? = null,

    /**
     * Overrides the default [kotlinx.coroutines.Dispatchers.IO].
     */
    @get:Provides
    @get:Named(Names.IO_DISPATCHER)
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,

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
    @get:Provides
    val timeSource: TimeSource = TimeSource.Monotonic,
) {
    @get:Provides
    internal val configuration: DivDebugConfiguration
        get() = this

    companion object {
        val Default = DivDebugConfiguration()
    }
}
