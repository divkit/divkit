package com.yandex.div.compose.images

import coil3.transform.Transformation

internal data class ImageRequestParams(
    val data: Any,
    val transformations: List<Transformation>
)
