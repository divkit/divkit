package com.yandex.div.steps

import android.view.View
import androidx.test.core.app.ActivityScenario
import com.yandex.div.Div2ImageLoaderScreenshotTest
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.StepsDsl
import org.json.JSONObject

internal fun imageLoaderScreenshot(f: ImageLoaderScreenshotSteps.() -> Unit) = f(ImageLoaderScreenshotSteps())

private val artifactsRelativePath = Div2ImageLoaderScreenshotTest::class.qualifiedName ?: ""

@StepsDsl
internal class ImageLoaderScreenshotSteps {

    fun runTest(
        scenario: ActivityScenario<DivScreenshotActivity>,
        casePath: String,
        testCase: JSONObject,
        loaderName: String,
    ) {
        scenario.onActivity {
            it.imageLoaderName = loaderName
            it.prepare()
            it.setDivData(testCase)
        }

        var divView: View? = null
        scenario.onActivity { divView = it.divView }
        val view = divView ?: return

        waitForLoadings(view)
        captureScreenshots(view, "$artifactsRelativePath/$loaderName", casePath)
    }
}
