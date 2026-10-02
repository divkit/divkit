package com.yandex.div.steps

import android.view.View
import com.yandex.div.rule.NetworkLoadingIdlingResource
import com.yandex.divkit.demo.Container
import com.yandex.test.idling.waitForIdlingResource

internal fun waitForLoadings(view: View) {
    try {
        waitForIdlingResource(
            NetworkLoadingIdlingResource(Container.imageLoader, Container.downloader, view)
        )
    } catch (e: Exception) {
        Container.imageLoader.resetIdle()
        Container.downloader.resetIdle()
        throw e
    }
}
