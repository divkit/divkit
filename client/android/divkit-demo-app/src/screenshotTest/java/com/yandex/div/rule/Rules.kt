@file:JvmName("ScreenshotTestRules")

package com.yandex.div.rule

import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.Div2ScreenshotTest
import com.yandex.div.steps.waitForLoadings
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.idling.waitForView
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.NoAnimationsRule
import com.yandex.test.screenshot.ScreenshotRule
import com.yandex.test.util.chain
import org.junit.rules.TestRule

fun baseRule(casePath: String, caseRoot: String, innerRule: TestRule): TestRule {
    return AllureMetadataRule(casePath, caseRoot)
        .chain(CheckCaseRule(casePath))
        .chain(NoAnimationsRule())
        .chain(ClosePopupsRule())
        .chain(innerRule)
}

fun screenshotRule(
    casePath: String,
    caseRoot: String,
    activityRule: ActivityParamsTestRule<out Activity>,
    relativePath: String = "",
    expectedSuite: String = "",
): TestRule {
    return screenshotRule(casePath, caseRoot, activityRule, relativePath, expectedSuite) {
        waitForLoadings(waitForView(DivScreenshotActivity.SCREENSHOT_VIEW_TAG))
    }
}

fun composeScreenshotRule(
    casePath: String,
    caseRoot: String,
    activityRule: ActivityParamsTestRule<DivComposeScreenshotActivity>,
    relativePath: String = "",
): TestRule {
    val compareWithView = InstrumentationRegistry.getArguments().getString("compareComposeWithView") == "true"
    val expectedSuite = if (compareWithView) Div2ScreenshotTest::class.qualifiedName ?: "" else ""
    return screenshotRule(casePath, caseRoot, activityRule, relativePath, expectedSuite) {
        waitForIdlingResource(ComposeIdlingResource(activityRule.activity.imageLoadingTracker))
    }
}

private fun screenshotRule(
    casePath: String,
    caseRoot: String,
    activityRule: ActivityParamsTestRule<out Activity>,
    relativePath: String,
    expectedSuite: String,
    waitForImages: () -> Unit
): TestRule {
    val screenshotRule = ScreenshotRule(casePath, relativePath, expectedSuite)
    screenshotRule.beforeScreenshotTaken(waitForImages)
    return baseRule(casePath, caseRoot, activityRule)
        .chain(screenshotRule)
}
