package com.yandex.div.compose.observer.events

/**
 * Snapshot of the gallery when the event occurs. The visible range includes partially
 * visible items. Indices are zero-based, or -1 when no item is visible.
 * [cardId] is the card log id; [galleryId] is null for an element without an id.
 */
@ConsistentCopyVisibility
data class DivGalleryScrollEvent internal constructor(
    val cardId: String,
    val galleryId: String?,
    val firstVisibleItem: Int,
    val lastVisibleItem: Int,
    val scrollDirection: DivScrollDirection,
)
