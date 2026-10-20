package com.yandex.div.rule

import androidx.test.core.app.ActivityScenario
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.SimpleIdlingResource

internal class UiReadyIdlingResource(
    private val scenario: ActivityScenario<DivScreenshotActivity>,
    private val checker: UiReadyChecker,
) : SimpleIdlingResource(pollingIntervalMillis = 16, description = "UiReadyIdlingResource") {

    private var frameSkipped = false

    override fun checkIdle(): Boolean {
        var ready = false
        scenario.onActivity { ready = checker.isReady(it.divView) }
        if (!ready) {
            frameSkipped = false
            return false
        }
        if (!frameSkipped) {
            frameSkipped = true
            return false
        }
        return true
    }
}
