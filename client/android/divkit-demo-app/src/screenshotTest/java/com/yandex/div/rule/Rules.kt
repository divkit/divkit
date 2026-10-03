@file:JvmName("ScreenshotTestRules")

package com.yandex.div.rule

import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.Div2ScreenshotTest
import com.yandex.div.steps.waitForLoadings
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivDataScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.idling.waitForView
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.NoAnimationsRule
import com.yandex.test.screenshot.ScreenshotRule
import com.yandex.test.util.chain
import org.json.JSONObject
import org.junit.rules.TestRule

fun baseRule(casePath: String, caseRoot: String, case: JSONObject, innerRule: TestRule): TestRule {
    return AllureMetadataRule(casePath, caseRoot)
        .chain(CheckCaseRule(case))
        .chain(NoAnimationsRule())
        .chain(ClosePopupsRule())
        .chain(innerRule)
}

fun screenshotRule(
    casePath: String,
    caseRoot: String,
    testCase: JSONObject,
    activityRule: ActivityParamsTestRule<DivScreenshotActivity>,
    relativePath: String = "",
    expectedSuite: String = "",
): TestRule {
    return screenshotRule(casePath, caseRoot, testCase, activityRule, relativePath, expectedSuite) {
        waitForLoadings(waitForView(DivScreenshotActivity.SCREENSHOT_VIEW_TAG))
    }
}

fun composeScreenshotRule(
    casePath: String,
    caseRoot: String,
    testCase: JSONObject,
    activityRule: ActivityParamsTestRule<DivComposeScreenshotActivity>,
    relativePath: String = "",
): TestRule {
    val compareWithView = InstrumentationRegistry.getArguments().getString("compareComposeWithView") == "true"
    val expectedSuite = if (compareWithView) Div2ScreenshotTest::class.qualifiedName ?: "" else ""
    return screenshotRule(casePath, caseRoot, testCase, activityRule, relativePath, expectedSuite) {
        waitForIdlingResource(ComposeIdlingResource(activityRule.activity.imageLoadingTracker))
    }
}

private fun screenshotRule(
    casePath: String,
    caseRoot: String,
    testCase: JSONObject,
    activityRule: ActivityParamsTestRule<out DivDataScreenshotActivity>,
    relativePath: String,
    expectedSuite: String,
    waitForImages: () -> Unit
): TestRule {
    val screenshotRule = ScreenshotRule(casePath, relativePath, expectedSuite)
    screenshotRule.beforeScreenshotTaken(waitForImages)
    return baseRule(casePath, caseRoot, testCase, activityRule)
        .chain(SetDataRule(testCase, activityRule))
        .chain(screenshotRule)
}
