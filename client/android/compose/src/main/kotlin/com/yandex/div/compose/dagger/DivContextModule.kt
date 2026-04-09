package com.yandex.div.compose.dagger

import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
internal object DivContextModule {

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

    @DivContextScope
    @Provides
    fun provideParsingErrorLogger(reporter: DivReporter): ParsingErrorLogger {
        return ParsingErrorLogger { reporter.reportError(it) }
    }

    @OptIn(InternalApi::class)
    @DivContextScope
    @Provides
    fun provideImageLoader(
        context: Context,
        debugConfiguration: DivDebugConfiguration
    ): ImageLoader {
        val debugImageLoader = debugConfiguration.imageLoaderProvider?.provide()
        if (debugImageLoader != null) {
            return debugImageLoader
        }
        return ImageLoader.Builder(context = context)
            .allowHardware(false)
            .build()
    }
}

private fun Context.getLifecycleOwner(): LifecycleOwner? {
    return when (this) {
        is LifecycleOwner -> this
        is ContextWrapper -> baseContext.getLifecycleOwner()
        else -> null
    }
}
