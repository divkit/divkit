package com.yandex.div.steps

import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.Div2ImageLoaderScreenshotTest
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.StepsDsl

internal fun imageLoaderScreenshot(f: ImageLoaderScreenshotSteps.() -> Unit) = f(ImageLoaderScreenshotSteps())

private val artifactsRelativePath = Div2ImageLoaderScreenshotTest::class.qualifiedName ?: ""

@StepsDsl
internal class ImageLoaderScreenshotSteps {

    fun runTest(
        activityRule: ActivityParamsTestRule<DivScreenshotActivity>,
        casePath: String,
        loaderName: String,
    ) {
        val params = Bundle()
        params.putString(DivScreenshotActivity.EXTRA_DIV_IMAGE_LOADER_NAME, loaderName)
        activityRule.launchActivity(params)

        val activity = activityRule.activity
        InstrumentationRegistry.getInstrumentation().runOnMainSync { activity.setDivData(casePath) }

        waitForImages()
        captureScreenshots(activity.divView, "$artifactsRelativePath/$loaderName", casePath)
    }
}
