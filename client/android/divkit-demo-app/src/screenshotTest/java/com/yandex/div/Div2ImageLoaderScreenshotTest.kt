package com.yandex.div

import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_COIL
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_GLIDE
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_PICASSO
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class Div2ImageLoaderScreenshotTest(private val case: String) {

    private val caseName = case
        .substringAfterLast(File.separator)
        .substringBeforeLast(CASE_EXTENSION)

    private val activityRule = ActivityParamsTestRule(
        activityClass = DivScreenshotActivity::class.java,
        launchActivity = false,
    )

    @get:Rule
    val rule = screenshotRule(name = caseName, casePath = case) { activityRule }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, relativePath = IMAGE_LOADER_PICASSO)
    fun divScreenshotPicasso() {
        launchActivityWith(IMAGE_LOADER_PICASSO)
    }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, relativePath = IMAGE_LOADER_GLIDE)
    fun divScreenshotGlide() {
        launchActivityWith(IMAGE_LOADER_GLIDE)
    }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, relativePath = IMAGE_LOADER_COIL)
    fun divScreenshotCoil() {
        launchActivityWith(IMAGE_LOADER_COIL)
    }

    private fun launchActivityWith(loaderName: String) {
        val params = Bundle()
        params.putString(DivScreenshotActivity.EXTRA_DIV_ASSET_NAME, case)
        params.putString(DivScreenshotActivity.EXTRA_DIV_IMAGE_LOADER_NAME, loaderName)
        activityRule.launchActivity(params)
    }

    companion object {

        private const val TEST_CASES_PATH = "ui_test_data/image-loaders"
        private const val CASE_EXTENSION = ".json"

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases() = AssetEnumerator(context).enumerate(TEST_CASES_PATH)
    }
}
