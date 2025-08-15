package com.yandex.div.core.downloader

/**
 * Observer for DivData internal changes.
 *
 * Unlike DivDataChangeObserver, the list of these observers is not
 * cleaned when DivData inside Div2View changes.
 */
internal interface PersistentDivDataObserver {
    /**
     * Called before DivData changed
     */
    fun onBeforeDivDataChanged() = Unit

    /**
     * Called after DivData changed
     */
    fun onAfterDivDataChanged() = Unit
}
