package com.yandex.div.compose

import android.graphics.Typeface
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.actions.DivActionLogger
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.histogram.DisabledHistogramConfiguration
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.compose.patch.DivPatchDownloader
import com.yandex.div.compose.preload.PreloadResult
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.DivAnimationsEnabledProvider
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
 *    val configuration = divConfiguration {
 *        reporter = MyReporter()
 *    }
 *    val divContext = DivContext(baseContext = activity, configuration = configuration)
 *    ComposeView(divContext).setContent {
 *        DivView(data = data)
 *    }
 */
@Module
class DivConfiguration private constructor(
    @get:Provides
    val actionHandler: DivExternalActionHandler,

    @get:Provides
    val actionLogger: DivActionLogger,

    @get:Provides
    val customViewFactories: Map<String, DivCustomViewFactory>,

    @get:Provides
    val extensionHandlers: Map<String, DivExtensionHandler>,

    @get:Provides
    val fontSourceProvider: DivFontSourceProvider,

    @get:Provides
    val histogramConfiguration: DivHistogramConfiguration,

    @get:Provides
    val imageLoaderConfiguration: ImageLoaderConfiguration,

    @get:Provides
    val playerFactory: DivVideoPlayerFactory,

    @get:Provides
    val reporter: DivReporter,

    @get:Provides
    val animationsEnabledProvider: DivAnimationsEnabledProvider,

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController,

    @get:Provides
    val videoPreloader: DivVideoPreloader,

    @get:Provides
    val patchDownloader: DivPatchDownloader,
) {
    @Deprecated(
        message = "Use divConfiguration { ... } or DivConfiguration.Builder instead.",
        level = DeprecationLevel.WARNING,
    )
    @JvmOverloads
    constructor(
        actionHandler: DivExternalActionHandler = defaultActionHandler,
        customViewFactories: Map<String, DivCustomViewFactory> = emptyMap(),
        extensionHandlers: Map<String, DivExtensionHandler> = emptyMap(),
        fontSourceProvider: DivFontSourceProvider = defaultFontSourceProvider,
        histogramConfiguration: DivHistogramConfiguration = DisabledHistogramConfiguration,
        imageLoaderConfiguration: ImageLoaderConfiguration = defaultImageLoaderConfiguration,
        playerFactory: DivVideoPlayerFactory = defaultDivVideoPlayerFactory,
        reporter: DivReporter = DivReporter(),
        animationsEnabledProvider: DivAnimationsEnabledProvider = DivAnimationsEnabledProvider.DEFAULT,
        variableController: DivVariableController = DivVariableController(),
        videoPreloader: DivVideoPreloader = defaultDivVideoPreloader,
    ) : this(
        actionHandler = actionHandler,
        actionLogger = defaultActionLogger,
        customViewFactories = customViewFactories,
        extensionHandlers = extensionHandlers,
        fontSourceProvider = fontSourceProvider,
        histogramConfiguration = histogramConfiguration,
        imageLoaderConfiguration = imageLoaderConfiguration,
        playerFactory = playerFactory,
        reporter = reporter,
        animationsEnabledProvider = animationsEnabledProvider,
        variableController = variableController,
        videoPreloader = videoPreloader,
        patchDownloader = defaultPatchDownloader,
    )

    /** Configures a [DivConfiguration] using the same defaults as its constructor. */
    class Builder {
        var actionHandler: DivExternalActionHandler = defaultActionHandler
        var actionLogger: DivActionLogger = defaultActionLogger
        var customViewFactories: Map<String, DivCustomViewFactory> = emptyMap()
        var extensionHandlers: Map<String, DivExtensionHandler> = emptyMap()
        var fontSourceProvider: DivFontSourceProvider = defaultFontSourceProvider
        var histogramConfiguration: DivHistogramConfiguration = DisabledHistogramConfiguration
        var imageLoaderConfiguration: ImageLoaderConfiguration = defaultImageLoaderConfiguration
        var playerFactory: DivVideoPlayerFactory = defaultDivVideoPlayerFactory
        var reporter: DivReporter = DivReporter()
        var animationsEnabledProvider: DivAnimationsEnabledProvider = DivAnimationsEnabledProvider.DEFAULT
        var variableController: DivVariableController = DivVariableController()
        var videoPreloader: DivVideoPreloader = defaultDivVideoPreloader
        var patchDownloader: DivPatchDownloader = defaultPatchDownloader

        fun build(): DivConfiguration = DivConfiguration(
            actionHandler = actionHandler,
            actionLogger = actionLogger,
            customViewFactories = customViewFactories,
            extensionHandlers = extensionHandlers,
            fontSourceProvider = fontSourceProvider,
            histogramConfiguration = histogramConfiguration,
            imageLoaderConfiguration = imageLoaderConfiguration,
            playerFactory = playerFactory,
            reporter = reporter,
            animationsEnabledProvider = animationsEnabledProvider,
            variableController = variableController,
            videoPreloader = videoPreloader,
            patchDownloader = patchDownloader,
        )
    }
}

/** Creates a [DivConfiguration] using a [DivConfiguration.Builder] block. */
fun divConfiguration(block: DivConfiguration.Builder.() -> Unit): DivConfiguration =
    DivConfiguration.Builder().apply(block).build()

private val defaultActionHandler = object : DivExternalActionHandler {}

private val defaultActionLogger = DivActionLogger { _, _ -> }

private val defaultPatchDownloader = DivPatchDownloader {
    throw UnsupportedOperationException("DivPatchDownloader is not configured")
}

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
    override suspend fun preloadVideoWithResult(sources: List<Uri>) = PreloadResult(true)
}

private val defaultImageLoaderConfiguration = object : ImageLoaderConfiguration {}
