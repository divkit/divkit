package com.yandex.div.compose.observer.events

/**
 * A newly selected tab. [selectedTab] is its zero-based index after scrolling settles.
 * [cardId] is the card log id. [tabsId] is null for an element without an id.
 */
@ConsistentCopyVisibility
data class DivTabChangedEvent internal constructor(
    val cardId: String,
    val tabsId: String?,
    val selectedTab: Int,
)
