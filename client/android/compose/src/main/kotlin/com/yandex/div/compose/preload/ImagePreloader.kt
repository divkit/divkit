package com.yandex.div.compose.preload

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

internal interface ImagePreloader {
    suspend fun preloadImages(
        div: Div,
        resolver: ExpressionResolver,
    )
}
