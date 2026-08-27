package com.yandex.div.compose.dagger

import android.content.Context
import coil3.ImageLoader
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.context.DivViewContextFactory
import com.yandex.div.compose.context.DivViewContextStorage
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DivFontFamilyCache
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.images.ImageRequestFactory
import com.yandex.div.compose.images.ImageRequestListener
import com.yandex.div.compose.images.ImagePreviewDecoder
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.internal.DivDebugFeatures
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.preload.DivPreloader
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope

@DivContextScope
@Component(
    modules = [
        DivConfiguration::class,
        DivContextModule::class
    ]
)
internal interface DivContextComponent {

    val animationConfiguration: AnimationConfiguration
    val baseContext: Context
    val coroutineScope: CoroutineScope
    val customViewFactories: Map<String, DivCustomViewFactory>
    val debugConfiguration: DivDebugConfiguration
    val debugFeatures: DivDebugFeatures
    val extensionHandlers: Map<String, DivExtensionHandler>
    val fontSourceProvider: DivFontSourceProvider
    val fontFamilyCache: DivFontFamilyCache
    val imageLoader: ImageLoader
    val imagePreviewDecoder: ImagePreviewDecoder
    val imageRequestFactory: ImageRequestFactory
    val imageRequestListener: ImageRequestListener
    val playerFactory: DivVideoPlayerFactory
    val preloader: DivPreloader
    val viewContextFactory: DivViewContextFactory
    val viewContextStorage: DivViewContextStorage

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController

    fun viewComponent(): DivViewComponent.Builder

    @Component.Builder
    interface Builder {
        fun build(
            @BindsInstance baseContext: Context,
            @BindsInstance configuration: DivConfiguration,
            @BindsInstance debugConfiguration: DivDebugConfiguration
        ): DivContextComponent
    }
}
