package com.yandex.div.compose.views

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import com.yandex.div.compose.views.modifiers.MatchParentSize

@Immutable
internal data class DivBlockLayoutParams(
    val matchParentWidth: MatchParentSize = MatchParentSize.Fill,
    val matchParentHeight: MatchParentSize = MatchParentSize.Fill,
    val defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
    val blockTapPropagation: Boolean = false,
    /** Whether service layouts separate the element from the layout of its parent element. */
    val hasLayoutWrapper: Boolean = false,
) {
    companion object {
        val Default = DivBlockLayoutParams()
    }
}
