@file:JvmName("ScreenshotTestRules")

package com.yandex.div.rule

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.Div2ScreenshotTest
import com.yandex.div.steps.waitForLoadings
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivDataScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.idling.waitForView
import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.NoAnimationsRule
import com.yandex.test.screenshot.ScreenshotRule
import com.yandex.test.util.chain
import org.json.JSONObject
import org.junit.rules.TestRule

fun classRule(activityRule: ActivityScenarioRule<*>): TestRule {
    return NoAnimationsRule()
        .chain(ClosePopupsRule())
        .chain(activityRule)
        .chain(ActivityClassRule(activityRule))
}

fun baseScreenshotTestRule(casePath: String, caseRoot: String, case: JSONObject) =
    AllureMetadataRule(casePath, caseRoot).chain(CheckCaseRule(case))

fun screenshotRule(
    casePath: String,
    caseRoot: String,
    testCase: JSONObject,
    activityRule: ActivityScenarioRule<DivScreenshotActivity>,
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
    activityRule: ActivityScenarioRule<DivComposeScreenshotActivity>,
    relativePath: String = "",
): TestRule {
    val compareWithView = InstrumentationRegistry.getArguments().getString("compareComposeWithView") == "true"
    val expectedSuite = if (compareWithView) Div2ScreenshotTest::class.qualifiedName ?: "" else ""
    return screenshotRule(casePath, caseRoot, testCase, activityRule, relativePath, expectedSuite) {
        waitForIdlingResource(ComposeIdlingResource(activityRule))
    }
}

private fun screenshotRule(
    casePath: String,
    caseRoot: String,
    testCase: JSONObject,
    activityRule: ActivityScenarioRule<out DivDataScreenshotActivity>,
    relativePath: String,
    expectedSuite: String,
    waitForImages: () -> Unit
): TestRule {
    val screenshotRule = ScreenshotRule(casePath, relativePath, expectedSuite)
    screenshotRule.beforeScreenshotTaken(waitForImages)
    return baseScreenshotTestRule(casePath, caseRoot, testCase)
        .chain(SetDataRule(testCase, activityRule))
        .chain(screenshotRule)
}
