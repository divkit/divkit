package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.baseRule
import com.yandex.div.steps.imageLoaderScreenshot
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

@RunWith(Parameterized::class)
class Div2ImageLoaderScreenshotTest(private val case: String) {

    private val activityRule = ActivityParamsTestRule(
        activityClass = DivScreenshotActivity::class.java,
        launchActivity = false,
    )

    @get:Rule
    val rule = baseRule(case, activityRule)

    @Test
    @Screenshot(viewId = R.id.screenshot_view)
    fun divScreenshotPicasso() {
        launchActivityWith(IMAGE_LOADER_PICASSO)
    }

    @Test
    @Screenshot(viewId = R.id.screenshot_view)
    fun divScreenshotGlide() {
        launchActivityWith(IMAGE_LOADER_GLIDE)
    }

    @Test
    @Screenshot(viewId = R.id.screenshot_view)
    fun divScreenshotCoil() {
        launchActivityWith(IMAGE_LOADER_COIL)
    }

    private fun launchActivityWith(loaderName: String) {
        imageLoaderScreenshot { runTest(activityRule, case, loaderName) }
    }

    companion object {

        private const val TEST_CASES_PATH = "ui_test_data/image-loaders"

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases() = AssetEnumerator(context).enumerate(TEST_CASES_PATH)
    }
}
