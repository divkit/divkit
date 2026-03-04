package com.yandex.div.steps

import com.yandex.div.rule.ImageLoadingIdlingResource
import com.yandex.divkit.demo.Container
import com.yandex.test.idling.waitForIdlingResource

internal fun waitForImages() {
    try {
        waitForIdlingResource(ImageLoadingIdlingResource(Container.imageLoader))
    } catch (e: Exception) {
        Container.imageLoader.resetIdle()
        throw e
    }
}
