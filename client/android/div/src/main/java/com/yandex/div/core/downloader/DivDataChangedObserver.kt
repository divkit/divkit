package com.yandex.div.core.downloader

import com.yandex.div2.DivData

/**
 * Observer for DivData internal changes
 */
interface DivDataChangedObserver {

    /**
     * Called when patch was applied
     */
    fun onDivPatchApplied(divData: DivData)
}
