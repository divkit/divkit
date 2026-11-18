package com.yandex.div.compose.di

import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.network.DivNetworkClient
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineDispatcher

@BindingContainer
internal class DivConfigurationModule(
    configuration: DivConfiguration,
    debugConfiguration: DivDebugConfiguration,
) {
    @get:Provides
    val configuration: DivConfiguration = configuration

    @get:Provides
    val debugConfiguration: DivDebugConfiguration = debugConfiguration

    @get:Provides
    val actionHandler: DivExternalActionHandler = configuration.actionHandler

    @get:Provides
    val animationsEnabledProvider: DivAnimationsEnabledProvider =
        configuration.animationsEnabledProvider

    @get:Provides
    val customViewFactories: Map<String, DivCustomViewFactory> = configuration.customViewFactories

    @get:Provides
    val extensionHandlers: Map<String, DivExtensionHandler> = configuration.extensionHandlers

    @get:Provides
    val fontSourceProvider: DivFontSourceProvider = configuration.fontSourceProvider

    @get:Provides
    val histogramConfiguration: DivHistogramConfiguration = configuration.histogramConfiguration

    @get:Provides
    val imageLoaderConfiguration: ImageLoaderConfiguration = configuration.imageLoaderConfiguration

    @get:Provides
    val playerFactory: DivVideoPlayerFactory = configuration.playerFactory

    @get:Provides
    val networkClient: DivNetworkClient = configuration.networkClient

    @get:Provides
    val reporter: DivReporter = configuration.reporter

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController = configuration.variableController

    @get:Provides
    val videoPreloader: DivVideoPreloader = configuration.videoPreloader

    @get:Provides
    @get:Named(Names.IO_DISPATCHER)
    val ioDispatcher: CoroutineDispatcher = debugConfiguration.ioDispatcher

    @get:Provides
    val timeSource: TimeSource = debugConfiguration.timeSource
}
