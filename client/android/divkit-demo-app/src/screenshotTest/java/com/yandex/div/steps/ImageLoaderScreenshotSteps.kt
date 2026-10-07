package com.yandex.div.steps

import com.yandex.div.Div2ImageLoaderScreenshotTest
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.runOnMainSync
import org.json.JSONObject

internal fun imageLoaderScreenshot(f: ImageLoaderScreenshotSteps.() -> Unit) = f(ImageLoaderScreenshotSteps())

private val artifactsRelativePath = Div2ImageLoaderScreenshotTest::class.qualifiedName ?: ""

@StepsDsl
internal class ImageLoaderScreenshotSteps {

    fun runTest(
        activityRule: ActivityParamsTestRule<DivScreenshotActivity>,
        casePath: String,
        testCase: JSONObject,
        loaderName: String,
    ) {
        val activity = activityRule.activity
        runOnMainSync {
            activity.imageLoaderName = loaderName
            activity.prepare()
            activity.setDivData(testCase)
        }

        waitForLoadings(activity.divView)
        captureScreenshots(activity.divView, "$artifactsRelativePath/$loaderName", casePath)
    }
}
