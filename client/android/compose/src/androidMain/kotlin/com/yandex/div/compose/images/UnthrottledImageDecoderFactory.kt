package com.yandex.div.compose.images

import android.os.Build
import coil3.Extras
import coil3.ImageLoader
import coil3.decode.BitmapFactoryDecoder
import coil3.decode.Decoder
import coil3.decode.StaticImageDecoder
import coil3.fetch.SourceFetchResult
import coil3.getExtra
import coil3.request.Options
import kotlinx.coroutines.sync.Semaphore

internal object UnthrottledImageDecoderFactory : Decoder.Factory {

    private val staticFactory: Decoder.Factory? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        StaticImageDecoder.Factory(Semaphore(Int.MAX_VALUE))
    } else {
        null
    }

    override fun create(result: SourceFetchResult, options: Options, imageLoader: ImageLoader): Decoder? {
        if (!options.getExtra(enabled)) return null

        // Preview decoders never wait for Coil's shared background decode permits.
        return staticFactory?.create(result, options, imageLoader)
            ?: BitmapFactoryDecoder(result.source, options)
    }

    val enabled = Extras.Key(false)
}
