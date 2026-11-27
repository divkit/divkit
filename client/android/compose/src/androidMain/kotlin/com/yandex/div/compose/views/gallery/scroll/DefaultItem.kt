package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import com.yandex.div2.DivGallery
import kotlinx.coroutines.flow.first

/**
 * [DivGallery.defaultItem] of the first composition and whether it is still to be applied. Remembered outside
 * the layout branches of a gallery: the item is applied once, by the scrollable layout that is composed.
 */
internal class DefaultItem(val index: Int, val isApplied: MutableState<Boolean>)

@Composable
internal fun rememberDefaultItem(defaultItem: Int): DefaultItem {
    val index = remember { defaultItem }
    val isApplied = rememberSaveable { mutableStateOf(index <= 0) }
    return remember(isApplied) { DefaultItem(index, isApplied) }
}

/**
 * Scrolls [layout] to [defaultItem] once it has items and a viewport: later values of the property and of
 * [alignment] do not move the gallery, and neither does a restored state.
 */
@Composable
internal fun ApplyDefaultItem(
    layout: GalleryScrollLayout,
    defaultItem: DefaultItem,
    alignment: DivGallery.ContentAlignment,
) {
    val currentAlignment by rememberUpdatedState(alignment)
    LaunchedEffect(layout, defaultItem) {
        if (defaultItem.isApplied.value) return@LaunchedEffect
        val info = snapshotFlow { layout.layoutInfo }.first { it.itemCount > 0 && it.viewportSize > 0 }
        val destination = GalleryScrollDestination(defaultItem.index.coerceAtMost(info.itemCount - 1))
        layout.scrollToItem(destination, animated = false, currentAlignment)
        defaultItem.isApplied.value = true
    }
}
