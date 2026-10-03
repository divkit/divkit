package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.baseRule
import com.yandex.div.steps.imageLoaderScreenshot
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_COIL
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_GLIDE
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Div2ImageLoaderScreenshotTest(private val casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)
    private val activityRule = ActivityParamsTestRule(
        activityClass = DivScreenshotActivity::class.java,
        launchActivity = false,
    )

    @get:Rule
    val rule = baseRule(casePath, TEST_CASES_PATH, testCase, activityRule)

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun testGlide() {
        launchActivityWith(IMAGE_LOADER_GLIDE)
    }

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun testCoil() {
        launchActivityWith(IMAGE_LOADER_COIL)
    }

    private fun launchActivityWith(loaderName: String) {
        imageLoaderScreenshot { runTest(activityRule, casePath, testCase, loaderName) }
    }

    companion object {

        private const val TEST_CASES_PATH = "ui_test_data/image-loaders"

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases() = AssetEnumerator().enumerate(TEST_CASES_PATH).withEscapedParameter()
    }
}
