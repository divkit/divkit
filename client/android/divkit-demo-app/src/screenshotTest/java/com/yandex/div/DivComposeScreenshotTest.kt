package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.Div2ScreenshotTest.Companion.TEST_CASES_PATH
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.classRule
import com.yandex.div.rule.composeScreenshotRule
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.screenshot.Screenshot
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DivComposeScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)

    @get:Rule
    val rule = composeScreenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule, casePath.relativePath)

    @Screenshot(viewTag = DivComposeScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() = Unit

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        private val activityRule = ActivityScenarioRule(DivComposeScreenshotActivity::class.java)

        @JvmField
        @ClassRule
        val classRule = classRule(activityRule)

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val enumerator = AssetEnumerator()
            return enumerator.enumerate(TEST_CASES_PATH)
                .filter { includedFiles.contains(it) }
                .let(enumerator::requireSelectedCase)
                .withEscapedParameter(prefix = "$TEST_CASES_PATH/")
        }
    }
}

// Run only tests that are not supported by RoborazziScreenshotTest.
private val includedFiles = setOf(
    // Image blur uses RenderScript, which is not supported in Robolectric.
    "snapshot_test_data/div-background/blur.json",
    "snapshot_test_data/div-image/blur-with-big-radius.json",
    "snapshot_test_data/div-image/blur.json",
    "snapshot_test_data/div-text/blur-background.json",
    "snapshot_test_data/image-formats/animated-webp/animated_webp_background_blur.json",
    "snapshot_test_data/image-formats/animated-webp/animated_webp_image_blur.json",
    "snapshot_test_data/image-formats/animated-webp/animated_webp_preview_blur.json",
    "snapshot_test_data/image-formats/gif/gif_background_blur.json",
    "snapshot_test_data/image-formats/gif/gif_image_blur.json",
    "snapshot_test_data/image-formats/gif/gif_preview_blur.json",
    "snapshot_test_data/image-formats/png/png_background_blur.json",
    "snapshot_test_data/image-formats/png/png_image_blur.json",
    "snapshot_test_data/image-formats/png/png_preview_blur.json",
    "snapshot_test_data/image-formats/webp/webp_background_blur.json",
    "snapshot_test_data/image-formats/webp/webp_image_blur.json",
    "snapshot_test_data/image-formats/webp/webp_preview_blur.json",
)
