package com.yandex.div.compose.images

import android.os.Build
import coil3.decode.Decoder
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.yandex.div.core.annotations.InternalApi

@InternalApi
fun gifDecoderFactory(): Decoder.Factory {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        AnimatedImageDecoder.Factory()
    } else {
        GifDecoder.Factory()
    }
}
