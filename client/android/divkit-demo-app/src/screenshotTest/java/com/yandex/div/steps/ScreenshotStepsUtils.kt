package com.yandex.div.steps

import com.yandex.div.rule.ImageLoadingIdlingResource
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import javax.inject.Provider

internal fun waitForImages(activityProvider: Provider<DivScreenshotActivity?>) {
    try {
        waitForIdlingResource(ImageLoadingIdlingResource(Container.imageLoader, activityProvider))
    } catch (e: Exception) {
        Container.imageLoader.resetIdle()
        throw e
    }
}
