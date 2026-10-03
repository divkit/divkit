package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.divFocus
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Div2InputHighlightScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)
    private val activityRule = ActivityParamsTestRule(DivScreenshotActivity::class.java)

    @get:Rule
    val rule = screenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule)

    @Test
    @Screenshot(
        viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG,
        name = "highlight_color_initial"
    )
    fun testInitialColor() {
        divFocus { clickOnTopInput() }
    }

    @Test
    @Screenshot(
        viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG,
        name = "highlight_color_changed"
    )
    fun testChangedColor() {
        divInput { clickOnActionButton() }
        divFocus { clickOnTopInput() }
    }

    companion object {

        private const val TEST_CASES_PATH = "ui_test_data/input"

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return AssetEnumerator()
                .enumerate(TEST_CASES_PATH)
                .filter { filename -> filename.endsWith("/div_input_highlight.json") }
                .withEscapedParameter()
        }
    }
}
