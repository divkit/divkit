package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.yandex.div2.DivTabs

@Immutable
internal data class TabTitleAppearance(
    val style: DivTabs.TabTitleStyle,
    val titleDelimiter: DivTabs.TabTitleDelimiter?,
    val itemSpacing: Dp,
)
