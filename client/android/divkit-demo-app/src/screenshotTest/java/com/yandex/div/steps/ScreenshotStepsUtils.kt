package com.yandex.div.steps

import com.yandex.div.rule.NetworkLoadingIdlingResource
import com.yandex.divkit.demo.Container
import com.yandex.test.idling.waitForIdlingResource

internal fun waitForLoadings() {
    try {
        waitForIdlingResource(
            NetworkLoadingIdlingResource(Container.imageLoader, Container.downloader)
        )
    } catch (e: Exception) {
        Container.imageLoader.resetIdle()
        Container.downloader.resetIdle()
        throw e
    }
}
