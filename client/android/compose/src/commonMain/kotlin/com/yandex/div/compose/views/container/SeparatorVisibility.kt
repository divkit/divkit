package com.yandex.div.compose.views.container

import androidx.compose.runtime.Immutable

@Immutable
internal data class SeparatorVisibility(
    val showAtStart: Boolean,
    val showBetween: Boolean,
    val showAtEnd: Boolean,
) {
    val hasAnySeparator: Boolean
        get() = showAtStart || showBetween || showAtEnd

    companion object {
        val None = SeparatorVisibility(
            showAtStart = false,
            showBetween = false,
            showAtEnd = false,
        )
    }
}
