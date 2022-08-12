package com.yandex.div.core.downloader

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div2.DivData

/**
 * Observer for DivData internal changes
 */
@PublicApi
interface DivDataChangedObserver {

    /**
     * Called when patch was applied
     */
    fun onDivPatchApplied(divData: DivData)
}
