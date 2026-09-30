package com.yandex.div

import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.divFocus
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Div2InputHighlightScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivScreenshotActivity::class.java,
        DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @get:Rule
    val rule = screenshotRule(case, TEST_CASES_PATH, activityRule)

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
