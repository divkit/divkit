@file:JvmName("ScreenshotTestRules")

package com.yandex.div.rule

import android.app.Activity
import com.yandex.div.steps.waitForImages
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
): TestRule {
    val screenshotRule = ScreenshotRule(relativePath, casePath)
    screenshotRule.beforeScreenshotTaken { waitForImages() }
    return baseRule(casePath, activityRule)
        .chain(screenshotRule)
}
