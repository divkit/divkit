package com.yandex.div.test.data

import android.net.Uri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivGifImage

fun gifImage(
    url: Expression<Uri>,
    preloadRequired: Expression<Boolean> = constant(false),
) = Div.GifImage(
    DivGifImage(
        gifUrl = url,
        preloadRequired = preloadRequired,
    )
)
