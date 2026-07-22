package com.yandex.div.steps

import com.yandex.div.rule.NetworkLoadingIdlingResource
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import javax.inject.Provider

internal fun waitForLoadings(activityProvider: Provider<DivScreenshotActivity?>) {
    try {
        waitForIdlingResource(
            NetworkLoadingIdlingResource(Container.imageLoader, Container.downloader, activityProvider)
        )
    } catch (e: Exception) {
        Container.imageLoader.resetIdle()
        Container.downloader.resetIdle()
        throw e
    }
}
