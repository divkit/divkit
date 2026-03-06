package com.yandex.div.rule

import com.yandex.divkit.demo.div.DemoDivImageLoaderWrapper
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.SimpleIdlingResource
import javax.inject.Provider

class ImageLoadingIdlingResource(
    private val imageLoader: DemoDivImageLoaderWrapper,
    private val activityProvider: Provider<DivScreenshotActivity?>,
    private val waitForNextFrame: Boolean = true
) : SimpleIdlingResource(description = "ImageLoadingIdlingResource") {

    private var loadingFinished = false
    private var frameSkipped = false

    override fun checkIdle(): Boolean {
        loadingFinished = imageLoader.isIdle
        if (!loadingFinished) {
            return false
        }
        if (waitForNextFrame && !frameSkipped) {
            activityProvider.get()?.stopAnimations()
            frameSkipped = true
            return false
        }
        return true
    }
}
