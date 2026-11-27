package com.yandex.div.compose.observer.events

/**
 * A user scroll of the tab title strip. This does not imply a change of the selected tab.
 * [selectedTab] is the zero-based index of the last settled tab when the event occurs.
 * [cardId] is the card log id. [tabsId] is null for an element without an id.
 */
@ConsistentCopyVisibility
public data class DivTabTitlesScrolledEvent internal constructor(
    val cardId: String,
    val tabsId: String?,
    val selectedTab: Int,
)
