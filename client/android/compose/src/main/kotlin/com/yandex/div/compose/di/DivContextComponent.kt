package com.yandex.div.compose.di

import android.content.Context
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.context.DivViewContextFactory
import com.yandex.div.compose.context.DivViewContextStorage
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.focus.InputFocus
import com.yandex.div.compose.font.DivFontFamilyCache
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.font.LineMetricsCache
import com.yandex.div.compose.images.ImageRequestFactory
import com.yandex.div.compose.internal.DivDebugFeatures
import com.yandex.div.compose.patch.DivPatcher
import com.yandex.div.compose.preload.DivPreloader
import com.yandex.div.compose.storedvalues.DivStoredValuesStorageFactory
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope

@DivContextScope
@DependencyGraph(
    bindingContainers = [
        DivContextModule::class,
        DivContextPlatformModule::class,
    ]
)
internal interface DivContextComponent : DivContextDependencies {

    val baseContext: Context
    val configuration: DivConfiguration
    val debugFeatures: DivDebugFeatures
    val extensionHandlers: Map<String, DivExtensionHandler>
    val fontSourceProvider: DivFontSourceProvider
    val fontFamilyCache: DivFontFamilyCache
    val imageRequestFactory: ImageRequestFactory
    val inputFocus: InputFocus
    val lineMetricsCache: LineMetricsCache
    val patcher: DivPatcher
    val preloader: DivPreloader
    val storedValuesStorageFactory: DivStoredValuesStorageFactory
    val viewContextFactory: DivViewContextFactory
    val viewContextStorage: DivViewContextStorage

    @get:Named(Names.MAIN_COROUTINE_SCOPE)
    val mainCoroutineScope: CoroutineScope

    fun viewComponent(): DivViewComponent.Builder

    @DependencyGraph.Factory
    interface Builder {
        fun build(
            @Provides baseContext: Context,
            @Includes configuration: DivConfigurationModule,
        ): DivContextComponent
    }
}
