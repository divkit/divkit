package com.yandex.div.steps

import androidx.test.core.app.ActivityScenario
import com.yandex.div.rule.UiReadyChecker
import com.yandex.div.rule.UiReadyIdlingResource
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource

internal fun waitForUiReady(
    scenario: ActivityScenario<DivScreenshotActivity>,
    checker: UiReadyChecker = UiReadyChecker(),
) {
    try {
        waitForIdlingResource(UiReadyIdlingResource(scenario, checker))
    } catch (e: Exception) {
        Container.imageLoader.resetIdle()
        Container.downloader.resetIdle()
        throw e
    }
}
