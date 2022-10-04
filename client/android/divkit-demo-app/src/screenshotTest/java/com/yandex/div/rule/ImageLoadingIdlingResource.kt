package com.yandex.div.rule

import com.yandex.divkit.demo.div.DemoDivImageLoader
import com.yandex.test.idling.SimpleIdlingResource
import javax.inject.Provider

class ImageLoadingIdlingResource(
    private val imageLoaderProvider: Provider<DemoDivImageLoader>,
    private val waitForNextFrame: Boolean = true
) : SimpleIdlingResource(description = "ImageLoadingIdlingResource") {

    private var loadingFinished = false
    private var frameSkipped = false

    constructor(
        imageLoader: DemoDivImageLoader,
        waitForNextFrame: Boolean = true
    ) : this(Provider { imageLoader }, waitForNextFrame)

    override fun checkIdle(): Boolean {
        loadingFinished = imageLoaderProvider.get().isIdle
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
