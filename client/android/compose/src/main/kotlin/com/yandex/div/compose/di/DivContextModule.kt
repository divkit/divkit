package com.yandex.div.compose.di

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.ImageLoader
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.images.ImageLoaderFactory
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.patch.DefaultPatchDownloader
import com.yandex.div.compose.patch.DivPatchDownloader
import com.yandex.div.compose.preload.CoilImagePreloader
import com.yandex.div.compose.preload.ImagePreloader
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.common.StoredValuesRepository
import com.yandex.div.storage.storedvalues.StoredValuesRepositoryImpl
import com.yandex.div2.DivExtension
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@BindingContainer
internal interface DivContextModule {

    @Binds
    fun bindImagePreloader(impl: CoilImagePreloader): ImagePreloader

    companion object {
        @DivContextScope
        @Provides
        fun provideAssetManager(context: Context): AssetManager = context.assets

        @DivContextScope
        @OptIn(ExperimentalTime::class)
        @Provides
        fun provideClock(): Clock = Clock.System

        @DivContextScope
        @Provides
        @Named(Names.MAIN_COROUTINE_SCOPE)
        fun provideMainCoroutineScope(
            context: Context,
            debugConfiguration: DivDebugConfiguration
        ): CoroutineScope {
            return debugConfiguration.coroutineScope
                ?: context.getLifecycleOwner()?.lifecycleScope
                ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        }

        @DivContextScope
        @Provides
        fun provideImageLoader(factory: ImageLoaderFactory): ImageLoader {
            return factory.createImageLoader()
        }

        @DivContextScope
        @Provides
        fun provideParsingErrorLogger(reporter: DivReporter): ParsingErrorLogger {
            return ParsingErrorLogger { reporter.reportError(it) }
        }

        @DivContextScope
        @Provides
        fun providePatchDownloader(
            configuration: DivConfiguration,
            defaultPatchDownloader: Lazy<DefaultPatchDownloader>,
        ): DivPatchDownloader {
            return configuration.patchDownloader ?: defaultPatchDownloader.value
        }

        @DivContextScope
        @Provides
        fun provideStorageComponent(context: Context): DivStorageComponent {
            return DivStorageComponent.create(
                context = context
            )
        }

        @DivContextScope
        @Provides
        fun provideStoredValuesRepository(
            storageComponent: DivStorageComponent
        ): StoredValuesRepository {
            return StoredValuesRepositoryImpl(
                rawJsonRepository = storageComponent.rawJsonRepository
            )
        }

        @DivContextScope
        @Provides
        fun provideWildcardExtensions(
            configuration: DivConfiguration,
        ): List<Pair<DivExtension, DivExtensionHandler>> {
            return configuration.wildcardExtensionHandlers
                .map { (id, handler) -> DivExtension(id = id) to handler }
        }
    }
}

private fun Context.getLifecycleOwner(): LifecycleOwner? {
    return when (this) {
        is LifecycleOwner -> this
        is ContextWrapper -> baseContext.getLifecycleOwner()
        else -> null
    }
}
