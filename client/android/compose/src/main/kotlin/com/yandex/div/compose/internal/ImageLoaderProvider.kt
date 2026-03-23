package com.yandex.div.compose.internal

import android.content.Context
import coil3.ImageLoader
import com.yandex.div.core.annotations.InternalApi

@InternalApi
fun interface ImageLoaderProvider {
    fun provide(context: Context): ImageLoader
}