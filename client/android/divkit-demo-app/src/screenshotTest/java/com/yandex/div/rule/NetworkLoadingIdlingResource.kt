package com.yandex.div.rule

import android.view.View
import com.yandex.divkit.demo.div.DemoDivDownloaderWrapper
import com.yandex.divkit.demo.div.DemoDivImageLoaderWrapper
import com.yandex.divkit.demo.screenshot.hasPendingImageDecoding
import com.yandex.test.idling.SimpleIdlingResource
import com.yandex.test.util.runOnMainSync

class NetworkLoadingIdlingResource(
    private val imageLoader: DemoDivImageLoaderWrapper,
    private val patchDownloader: DemoDivDownloaderWrapper,
    private val view: View,
    private val waitForNextFrame: Boolean = true
) : SimpleIdlingResource(pollingIntervalMillis = 16, description = "ImageLoadingIdlingResource") {

    private var loadingFinished = false
    private var frameSkipped = false

    override fun checkIdle(): Boolean {
        runOnMainSync {
            loadingFinished = imageLoader.isIdle && patchDownloader.isIdle &&
                view.isLaidOut && !view.isLayoutRequested && !view.hasPendingImageDecoding()
        }
        if (!loadingFinished) {
            frameSkipped = false
            return false
        }
        if (waitForNextFrame && !frameSkipped) {
            frameSkipped = true
            return false
        }
        return true
    }
}
