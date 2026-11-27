package com.yandex.div.compose.images

import androidx.compose.runtime.Composable
import coil3.request.ImageRequest
import com.yandex.div.compose.LocalDivContext

@Composable
internal fun rememberImageRequest(params: ImageRequestParams): ImageRequest =
    rememberImageRequest(params, LocalDivContext.current.component.imageRequestFactory)
