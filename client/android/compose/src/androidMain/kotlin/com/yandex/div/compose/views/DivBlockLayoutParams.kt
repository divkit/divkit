package com.yandex.div.compose.views

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import com.yandex.div.compose.views.modifiers.MatchParentSize

@Immutable
internal data class DivBlockLayoutParams(
    val applyMargins: Boolean = true,
    val matchParentWidth: MatchParentSize = MatchParentSize.Fill,
    val matchParentHeight: MatchParentSize = MatchParentSize.Fill,
    val defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
    val blockTapPropagation: Boolean = false,
) {
    companion object {
        val Default = DivBlockLayoutParams()
    }
}
