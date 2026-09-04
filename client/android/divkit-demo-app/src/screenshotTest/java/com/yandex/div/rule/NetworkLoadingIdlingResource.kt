package com.yandex.div.rule

import com.yandex.divkit.demo.div.DemoDivDownloaderWrapper
import com.yandex.divkit.demo.div.DemoDivImageLoaderWrapper
import com.yandex.test.idling.SimpleIdlingResource
import com.yandex.test.util.runOnMainSync

class NetworkLoadingIdlingResource(
    private val imageLoader: DemoDivImageLoaderWrapper,
    private val patchDownloader: DemoDivDownloaderWrapper,
    private val waitForNextFrame: Boolean = true
) : SimpleIdlingResource(pollingIntervalMillis = 16, description = "ImageLoadingIdlingResource") {

    private var loadingFinished = false
    private var frameSkipped = false

    override fun checkIdle(): Boolean {
        runOnMainSync { loadingFinished = imageLoader.isIdle && patchDownloader.isIdle }
        if (!loadingFinished) {
            return false
        }
        if (waitForNextFrame && !frameSkipped) {
            frameSkipped = true
            return false
        }
        return true
    }
}
