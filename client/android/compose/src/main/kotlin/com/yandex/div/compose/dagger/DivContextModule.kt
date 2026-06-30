package com.yandex.div.compose.dagger

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.images.DivkitAssetUriMapper
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.compose.images.gifDecoderFactory
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.preload.CoilImagePreloader
import com.yandex.div.compose.preload.ImagePreloader
import com.yandex.div.compose.utils.SystemTimeProvider
import com.yandex.div.compose.utils.TimeProvider
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.storedvalues.StoredValuesRepository
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.storedvalues.StoredValuesRepositoryImpl
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@Module
internal interface DivContextModule {

    @Binds
    fun bindImagePreloader(impl: CoilImagePreloader): ImagePreloader

    companion object {
        @DivContextScope
        @Provides
        fun provideAssetManager(context: Context): AssetManager = context.assets

        @DivContextScope
        @Provides
        fun provideCoroutineScope(
            context: Context,
            debugConfiguration: DivDebugConfiguration
        ): CoroutineScope {
            return debugConfiguration.coroutineScope
                ?: context.getLifecycleOwner()?.lifecycleScope
                ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        }

        @OptIn(InternalApi::class)
        @DivContextScope
        @Provides
        fun provideImageLoader(
            context: Context,
            imageLoaderConfiguration: ImageLoaderConfiguration,
        ): ImageLoader {
            return ImageLoader.Builder(context = context)
                .allowHardware(false)
                .components {
                    imageLoaderConfiguration.applyComponents(this)
                    add(DivkitAssetUriMapper())
                    add(gifDecoderFactory())
                }
                .eventListener(imageLoaderConfiguration.eventListener)
                .build()
        }

        @DivContextScope
        @Provides
        fun provideParsingErrorLogger(reporter: DivReporter): ParsingErrorLogger {
            return ParsingErrorLogger { reporter.reportError(it) }
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
        fun provideTimeProvider(): TimeProvider {
            return SystemTimeProvider()
        }

        @DivContextScope
        @OptIn(ExperimentalTime::class)
        @Provides
        fun provideTimeSource(
            debugConfiguration: DivDebugConfiguration
        ): TimeSource {
            return debugConfiguration.timeSource ?: TimeSource.Monotonic
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
