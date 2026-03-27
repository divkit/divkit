@file:JvmName("ScreenshotTestRules")

package com.yandex.div.rule

import android.app.Activity
import com.yandex.div.steps.waitForImages
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.NoAnimationsRule
import com.yandex.test.screenshot.ScreenshotRule
import com.yandex.test.util.chain
import org.junit.rules.TestRule

fun baseRule(casePath: String, innerRule: TestRule): TestRule {
    return CheckCaseRule(casePath)
        .chain(NoAnimationsRule())
        .chain(ClosePopupsRule())
        .chain(innerRule)
}

fun screenshotRule(
    casePath: String,
    activityRule: ActivityParamsTestRule<out Activity>,
    relativePath: String = "",
    expectedSuite: String = "",
): TestRule {
    return screenshotRule(casePath, activityRule, relativePath, expectedSuite) {
        waitForImages { activityRule.activity as? DivScreenshotActivity }
    }
}

fun composeScreenshotRule(
    casePath: String,
    activityRule: ActivityParamsTestRule<DivComposeScreenshotActivity>,
    relativePath: String = "",
): TestRule {
    return screenshotRule(casePath, activityRule, relativePath, "") {
        waitForIdlingResource(activityRule.activity.imageLoadingTracker)
    }
}

private fun screenshotRule(
    casePath: String,
    activityRule: ActivityParamsTestRule<out Activity>,
    relativePath: String,
    expectedSuite: String,
    waitForImages: () -> Unit
): TestRule {
    val screenshotRule = ScreenshotRule(casePath, relativePath, expectedSuite)
    screenshotRule.beforeScreenshotTaken(waitForImages)
    return baseRule(casePath, activityRule)
        .chain(screenshotRule)
}
