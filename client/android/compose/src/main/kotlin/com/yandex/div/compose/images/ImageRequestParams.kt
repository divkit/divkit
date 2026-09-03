package com.yandex.div.compose.images

import androidx.compose.runtime.Immutable
import coil3.transform.Transformation

@Immutable
internal data class ImageRequestParams(
    val data: Any,
    val transformations: List<Transformation> = emptyList(),
    val limitToDisplaySize: Boolean = false,
)
