package com.yandex.div.compose.views.container.wrap

import androidx.compose.runtime.Immutable
import com.yandex.div.compose.views.container.SeparatorVisibility

@Immutable
internal data class WrapSeparatorDrawParams(
    val itemSeparatorInfo: SeparatorDrawInfoPx?,
    val itemSeparatorVisibility: SeparatorVisibility,
    val lineSeparatorInfo: SeparatorDrawInfoPx?,
    val lineSeparatorVisibility: SeparatorVisibility,
    val mainAxisStartPadding: Float,
    val mainAxisEndPadding: Float,
    val crossAxisStartPadding: Float,
    val crossAxisEndPadding: Float,
)
