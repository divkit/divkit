package com.yandex.div.compose.di

import coil3.ImageLoader
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.images.ImagePreviewDecoder
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div2.DivExtension
import dev.zacsweers.metro.Named

internal interface DivContextDependencies {

    val animationConfiguration: AnimationConfiguration
    val customViewFactories: Map<String, DivCustomViewFactory>
    val debugConfiguration: DivDebugConfiguration
    val imageLoader: ImageLoader
    val imagePreviewDecoder: ImagePreviewDecoder
    val playerFactory: DivVideoPlayerFactory
    val wildcardExtensions: List<Pair<DivExtension, DivExtensionHandler>>

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController
}
