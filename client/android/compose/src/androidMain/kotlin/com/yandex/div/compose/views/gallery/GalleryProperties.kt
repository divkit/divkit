package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivGallery

/** Gallery properties observed once in [DivGalleryView] and shared by the list and grid layouts. */
@Immutable
internal data class GalleryProperties(
    val isHorizontal: Boolean,
    val columnCount: Int,
    val itemSpacing: Dp,
    /** Spacing between lanes; only grids have more than one lane. */
    val crossSpacing: Dp,
    val crossAlignment: CrossAxisAlignment,
    val scrollContentAlignment: DivGallery.ContentAlignment,
    val contentPadding: PaddingValues,
    val defaultItem: Int,
    val scrollMode: DivGallery.ScrollMode,
    val hasScrollbar: Boolean,
    val id: String?,
)

@Composable
internal fun DivGallery.observeProperties(): GalleryProperties {
    val spacing = itemSpacing.observedValue().toDp()
    val mode = scrollMode.observedValue()
    return GalleryProperties(
        isHorizontal = orientation.observedValue() == DivGallery.Orientation.HORIZONTAL,
        columnCount = columnCount.observedIntValue(1),
        itemSpacing = spacing,
        crossSpacing = crossSpacing?.observedValue()?.toDp() ?: spacing,
        crossAlignment = when (crossContentAlignment.observedValue()) {
            DivGallery.ContentAlignment.START -> CrossAxisAlignment.START
            DivGallery.ContentAlignment.CENTER -> CrossAxisAlignment.CENTER
            DivGallery.ContentAlignment.END -> CrossAxisAlignment.END
        },
        scrollContentAlignment = scrollContentAlignment?.observedValue() ?: mode.defaultScrollContentAlignment(),
        contentPadding = paddings.observeInsets(),
        defaultItem = defaultItem.observedIntValue(),
        scrollMode = mode,
        hasScrollbar = scrollbar.observedValue() != DivGallery.Scrollbar.NONE,
        id = id,
    )
}

internal fun DivGallery.ScrollMode.defaultScrollContentAlignment(): DivGallery.ContentAlignment =
    when (this) {
        DivGallery.ScrollMode.DEFAULT -> DivGallery.ContentAlignment.START
        DivGallery.ScrollMode.PAGING -> DivGallery.ContentAlignment.CENTER
    }
