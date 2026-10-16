package com.yandex.div.compose

import android.net.Uri
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DefaultFontSourceProvider
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.histogram.DisabledHistogramConfiguration
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.compose.patch.DefaultPatchDownloader
import com.yandex.div.compose.patch.DivPatchDownloader
import com.yandex.div.compose.preload.PreloadResult
import com.yandex.div.compose.video.DisabledVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
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
    val animationsEnabledProvider: DivAnimationsEnabledProvider,

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
    val patchDownloader: DivPatchDownloader,

    @get:Provides
    val playerFactory: DivVideoPlayerFactory,

    @get:Provides
    val networkClient: DivNetworkClient,

    @get:Provides
    val reporter: DivReporter,

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController,

    @get:Provides
    val videoPreloader: DivVideoPreloader,
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
        fontSourceProvider: DivFontSourceProvider = DefaultFontSourceProvider,
        histogramConfiguration: DivHistogramConfiguration = DisabledHistogramConfiguration,
        imageLoaderConfiguration: ImageLoaderConfiguration = defaultImageLoaderConfiguration,
        playerFactory: DivVideoPlayerFactory = DisabledVideoPlayerFactory,
        reporter: DivReporter = DivReporter(),
        animationsEnabledProvider: DivAnimationsEnabledProvider = DivAnimationsEnabledProvider.DEFAULT,
        variableController: DivVariableController = DivVariableController(),
        videoPreloader: DivVideoPreloader = disabledVideoPreloader,
    ) : this(
        actionHandler = actionHandler,
        animationsEnabledProvider = animationsEnabledProvider,
        customViewFactories = customViewFactories,
        extensionHandlers = extensionHandlers,
        fontSourceProvider = fontSourceProvider,
        histogramConfiguration = histogramConfiguration,
        imageLoaderConfiguration = imageLoaderConfiguration,
        networkClient = disabledNetworkClient,
        patchDownloader = DefaultPatchDownloader(
            networkClient = disabledNetworkClient,
            reporter = reporter,
        ),
        playerFactory = playerFactory,
        reporter = reporter,
        variableController = variableController,
        videoPreloader = videoPreloader,
    )

    /**
     * Configures a [DivConfiguration] using the same defaults as its constructor.
     */
    class Builder {
        var actionHandler: DivExternalActionHandler = defaultActionHandler
        var animationsEnabledProvider: DivAnimationsEnabledProvider =
            DivAnimationsEnabledProvider.DEFAULT
        var customViewFactories: Map<String, DivCustomViewFactory> = emptyMap()
        var extensionHandlers: Map<String, DivExtensionHandler> = emptyMap()
        var fontSourceProvider: DivFontSourceProvider = DefaultFontSourceProvider
        var histogramConfiguration: DivHistogramConfiguration = DisabledHistogramConfiguration
        var imageLoaderConfiguration: ImageLoaderConfiguration = defaultImageLoaderConfiguration
        var playerFactory: DivVideoPlayerFactory = DisabledVideoPlayerFactory
        var reporter: DivReporter = DivReporter()
        var networkClient: DivNetworkClient = disabledNetworkClient
        var variableController: DivVariableController = DivVariableController()
        var videoPreloader: DivVideoPreloader = disabledVideoPreloader

        private var _patchDownloader: DivPatchDownloader? = null

        @Deprecated("Use networkClient to customize network requests")
        var patchDownloader: DivPatchDownloader
            get() = _patchDownloader ?: DefaultPatchDownloader(networkClient, reporter)
            set(value) {
                _patchDownloader = value
            }

        fun build(): DivConfiguration = DivConfiguration(
            actionHandler = actionHandler,
            animationsEnabledProvider = animationsEnabledProvider,
            customViewFactories = customViewFactories,
            extensionHandlers = extensionHandlers,
            fontSourceProvider = fontSourceProvider,
            histogramConfiguration = histogramConfiguration,
            imageLoaderConfiguration = imageLoaderConfiguration,
            networkClient = networkClient,
            patchDownloader = patchDownloader,
            playerFactory = playerFactory,
            reporter = reporter,
            variableController = variableController,
            videoPreloader = videoPreloader,
        )
    }
}

/**
 * Creates a [DivConfiguration] using a [DivConfiguration.Builder] block.
 */
fun divConfiguration(block: DivConfiguration.Builder.() -> Unit): DivConfiguration =
    DivConfiguration.Builder().apply(block).build()

private val defaultActionHandler = object : DivExternalActionHandler {}

private val disabledNetworkClient = DivNetworkClient {
    throw UnsupportedOperationException("DivNetworkClient is not configured")
}

private val disabledVideoPreloader = object : DivVideoPreloader {
    override suspend fun preloadVideoWithResult(sources: List<Uri>) = PreloadResult(true)
}

private val defaultImageLoaderConfiguration = object : ImageLoaderConfiguration {}
