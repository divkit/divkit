package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div2.Div
import com.yandex.div2.DivGifImage

fun gifImage(
    url: String? = null,
    preloadRequired: Boolean = false
) = Div.GifImage(
    DivGifImage(
        gifUrl = url?.let { constant(it.toUri()) },
        preloadRequired = constant(preloadRequired)
    )
)
