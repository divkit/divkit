package com.yandex.div.compose.views

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp

@Immutable
internal data class DivBlockLayoutParams(
    val applyMargins: Boolean = true,
    val fillMatchParentWidth: Boolean = true,
    val fillMatchParentHeight: Boolean = true,
    val defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
    val suppressMatchParentIntrinsics: Boolean = false,
    val matchParentMaxWidth: Dp = Dp.Unspecified,
    val matchParentMaxHeight: Dp = Dp.Unspecified,
    val blockTapPropagation: Boolean = false,
) {
    companion object {
        val Default = DivBlockLayoutParams()
    }
}
