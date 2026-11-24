package com.yandex.div.compose.observer.events

/**
 * A newly selected pager page. [currentPageIndex] is zero-based in the actual page list,
 * including when the pager loops. [scrollDirection] is relative to page order.
 * [cardId] is the card log id; [pagerId] is null for an element without an id.
 */
@ConsistentCopyVisibility
data class DivPagerPageChangedEvent internal constructor(
    val cardId: String,
    val pagerId: String?,
    val currentPageIndex: Int,
    val scrollDirection: DivScrollDirection,
)
