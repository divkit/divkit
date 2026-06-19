package com.yandex.div.compose

import android.graphics.Typeface
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.histogram.DisabledHistogramConfiguration
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Named

/**
 * Entry point of DivKit `compose` library. Provides configuration for composing [DivView]s.
 *
 * Example usage:
 *
 *    val configuration = DivComposeConfiguration(
 *        reporter = MyReporter()
 *    )
 *    val divContext = DivContext(baseContext = activity, configuration = configuration)
 *    ComposeView(divContext).setContent {
 *        DivView(data = data)
 *    }
 */
@Module
@ExperimentalApi
class DivComposeConfiguration(
    @get:Provides
    val actionHandler: DivExternalActionHandler = defaultActionHandler,

    @get:Provides
    val customViewFactories: Map<String, DivCustomViewFactory> = emptyMap(),

    @get:Provides
    val extensionHandlers: Map<String, DivExtensionHandler> = emptyMap(),

    @get:Provides
    val fontSourceProvider: DivFontSourceProvider = defaultFontSourceProvider,

    @get:Provides
    val histogramConfiguration: DivHistogramConfiguration = DisabledHistogramConfiguration,

    @get:Provides
    val playerFactory: DivVideoPlayerFactory = defaultDivVideoPlayerFactory,

    @get:Provides
    val reporter: DivReporter = DivReporter(),

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController = DivVariableController(),

    @get:Provides
    val videoPreloader: DivVideoPreloader = defaultDivVideoPreloader,
)

private val defaultActionHandler = object : DivExternalActionHandler {}

private val defaultFontSourceProvider = object : DivFontSourceProvider {
    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        return DivFontSource.Typeface(Typeface.DEFAULT)
    }
}

private val defaultDivVideoPlayerFactory = DivVideoPlayerFactory {
    object : DivVideoPlayer {
        override val isReady: StateFlow<Boolean> = MutableStateFlow(false)
        override val isPlaying: StateFlow<Boolean> = MutableStateFlow(false)
        override val isBuffering: StateFlow<Boolean> = MutableStateFlow(false)
        override val isEnded: StateFlow<Boolean> = MutableStateFlow(false)
        override val error: StateFlow<Throwable?> = MutableStateFlow(null)
        override val currentTimeMs: StateFlow<Long> = MutableStateFlow(0L)
        override fun seek(timeMs: Long) = Unit
        override fun play() = Unit
        override fun pause() = Unit
        override fun release() = Unit
        @Composable
        override fun Content(config: DivVideoPlayerConfig, modifier: Modifier) = Unit
    }
}

private val defaultDivVideoPreloader = object : DivVideoPreloader {
    override suspend fun preloadVideo(sources: List<Uri>) = Unit
}
