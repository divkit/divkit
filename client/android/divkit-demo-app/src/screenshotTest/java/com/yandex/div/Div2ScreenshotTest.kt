package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.demo.screenshot.viewDeviceScreenshotCases
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class Div2ScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)
    private val activityRule = ActivityParamsTestRule(DivScreenshotActivity::class.java)

    @get:Rule
    val rule = screenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule, casePath.relativePath)

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() = Unit

    companion object {

        const val TEST_CASES_PATH = "snapshot_test_data"

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        private val ignoredCases = listOf(
            "snapshot_test_data/div-text/all_attributes.json",
            "snapshot_test_data/div-container/baseline-with-images.json",
        )

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val enumerator = AssetEnumerator()
            return enumerator.enumerate(TEST_CASES_PATH)
                .filter { !ignoredCases.contains(it) }
                .filter { it.removePrefix("$TEST_CASES_PATH/") in viewDeviceScreenshotCases }
                .let(enumerator::requireSelectedCase)
                .withEscapedParameter()
        }

        val String.relativePath: String
            get() {
                return substringAfter("$TEST_CASES_PATH${File.separator}")
                    .substringBeforeLast(File.separator)
            }
    }
}
