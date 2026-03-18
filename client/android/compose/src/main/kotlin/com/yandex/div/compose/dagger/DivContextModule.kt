package com.yandex.div.compose.dagger

import android.content.Context
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.DivReporter
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides

@Module
internal object DivContextModule {

    @DivContextScope
    @Provides
    fun provideParsingErrorLogger(reporter: DivReporter): ParsingErrorLogger {
        return ParsingErrorLogger { exception ->
            reporter.reportError("Parsing error", exception)
        }
    }

    @DivContextScope
    @Provides
    fun provideImageLoader(
        baseContext: Context
    ): ImageLoader {
        return ImageLoader.Builder(context = baseContext)
            .allowHardware(false)
            .build()
    }
}
