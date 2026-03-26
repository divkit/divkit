package com.yandex.div.compose.dagger

import android.content.Context
import coil3.ImageLoader
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.internal.ImageLoaderProvider
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides

@Module
internal object DivContextModule {

    @DivContextScope
    @Provides
    fun provideParsingErrorLogger(reporter: DivReporter): ParsingErrorLogger {
        return ParsingErrorLogger { reporter.reportError(it) }
    }

    @OptIn(InternalApi::class)
    @DivContextScope
    @Provides
    fun provideImageLoader(
        imageLoaderProvider: ImageLoaderProvider,
        context: Context
    ): ImageLoader {
        return imageLoaderProvider.provide(context)
    }
}
