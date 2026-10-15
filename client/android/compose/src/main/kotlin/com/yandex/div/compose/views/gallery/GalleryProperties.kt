package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.toDp
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivGallery

/** Gallery properties observed once in [DivGalleryView] and shared by the list and grid layouts. */
@Immutable
internal data class GalleryProperties(
    val isHorizontal: Boolean,
    val columnCount: Int,
    val itemSpacing: Dp,
    val crossSpacing: Dp,
    val crossContentAlignment: DivGallery.ContentAlignment,
    val scrollContentAlignment: DivGallery.ContentAlignment,
    val contentPadding: PaddingValues,
    val defaultItem: Int,
    val scrollMode: DivGallery.ScrollMode,
    val scrollbar: Expression<DivGallery.Scrollbar>,
)

@Composable
internal fun DivGallery.observeProperties(): GalleryProperties {
    val spacing = itemSpacing.observedValue().toDp()
    val columns = columnCount.observedIntValue(1)
    val mode = scrollMode.observedValue()
    return GalleryProperties(
        isHorizontal = orientation.observedValue() == DivGallery.Orientation.HORIZONTAL,
        columnCount = columns,
        itemSpacing = spacing,
        crossSpacing = if (columns > 1) crossSpacing?.observedValue()?.toDp() ?: spacing else spacing,
        crossContentAlignment = crossContentAlignment.observedValue(),
        scrollContentAlignment = scrollContentAlignment?.observedValue() ?: mode.defaultScrollContentAlignment(),
        contentPadding = paddings.observeInsets(),
        defaultItem = defaultItem.observedIntValue(),
        scrollMode = mode,
        scrollbar = scrollbar,
    )
}
