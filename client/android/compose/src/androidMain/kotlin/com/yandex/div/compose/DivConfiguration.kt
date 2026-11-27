package com.yandex.div.compose

import android.net.Uri
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DefaultFontSourceProvider
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.histogram.DisabledHistogramConfiguration
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.compose.observer.DivComponentObserver
import com.yandex.div.compose.patch.DivPatchDownloader
import com.yandex.div.compose.preload.PreloadResult
import com.yandex.div.compose.video.DisabledVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.network.DivNetworkClient

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
public class DivConfiguration private constructor(
    public val actionHandler: DivExternalActionHandler,
    public val animationsEnabledProvider: DivAnimationsEnabledProvider,
    public val customViewFactories: Map<String, DivCustomViewFactory>,
    public val extensionHandlers: Map<String, DivExtensionHandler>,
    public val fontSourceProvider: DivFontSourceProvider,
    public val histogramConfiguration: DivHistogramConfiguration,
    public val imageLoaderConfiguration: ImageLoaderConfiguration,
    public val patchDownloader: DivPatchDownloader?,
    public val playerFactory: DivVideoPlayerFactory,
    public val networkClient: DivNetworkClient,
    public val reporter: DivReporter,
    public val componentObserver: DivComponentObserver,
    public val variableController: DivVariableController,
    public val videoPreloader: DivVideoPreloader,
    public val wildcardExtensionHandlers: Map<String, DivExtensionHandler>,
) {

    @Deprecated(
        message = "Use divConfiguration { ... } or DivConfiguration.Builder instead.",
        level = DeprecationLevel.WARNING,
    )
    @JvmOverloads
    public constructor(
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
        patchDownloader = null,
        playerFactory = playerFactory,
        reporter = reporter,
        componentObserver = defaultComponentObserver,
        variableController = variableController,
        videoPreloader = videoPreloader,
        wildcardExtensionHandlers = emptyMap(),
    )

    /**
     * Configures a [DivConfiguration] using the same defaults as its constructor.
     */
    public class Builder {
        public var actionHandler: DivExternalActionHandler = defaultActionHandler
        public var animationsEnabledProvider: DivAnimationsEnabledProvider =
            DivAnimationsEnabledProvider.DEFAULT
        public var customViewFactories: Map<String, DivCustomViewFactory> = emptyMap()
        public var extensionHandlers: Map<String, DivExtensionHandler> = emptyMap()
        public var fontSourceProvider: DivFontSourceProvider = DefaultFontSourceProvider
        public var histogramConfiguration: DivHistogramConfiguration =
            DisabledHistogramConfiguration
        public var imageLoaderConfiguration: ImageLoaderConfiguration =
            defaultImageLoaderConfiguration
        public var playerFactory: DivVideoPlayerFactory = DisabledVideoPlayerFactory
        public var reporter: DivReporter = DivReporter()
        public var componentObserver: DivComponentObserver = defaultComponentObserver
        public var networkClient: DivNetworkClient = disabledNetworkClient
        public var variableController: DivVariableController = DivVariableController()
        public var videoPreloader: DivVideoPreloader = disabledVideoPreloader
        public var wildcardExtensionHandlers: Map<String, DivExtensionHandler> = emptyMap()

        @Deprecated("Use networkClient to customize network requests")
        public var patchDownloader: DivPatchDownloader? = null

        public fun build(): DivConfiguration = DivConfiguration(
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
            componentObserver = componentObserver,
            variableController = variableController,
            videoPreloader = videoPreloader,
            wildcardExtensionHandlers = wildcardExtensionHandlers,
        )
    }
}

/**
 * Creates a [DivConfiguration] using a [DivConfiguration.Builder] block.
 */
public fun divConfiguration(block: DivConfiguration.Builder.() -> Unit): DivConfiguration =
    DivConfiguration.Builder().apply(block).build()

private val defaultActionHandler = object : DivExternalActionHandler {}

internal val defaultComponentObserver = object : DivComponentObserver {}

private val disabledNetworkClient = DivNetworkClient {
    error("DivNetworkClient is not configured")
}

private val disabledVideoPreloader = object : DivVideoPreloader {
    override suspend fun preloadVideoWithResult(sources: List<Uri>) = PreloadResult(true)
}

private val defaultImageLoaderConfiguration = object : ImageLoaderConfiguration {}
