package com.yandex.div

import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.divFocus
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.R
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
    val rule = screenshotRule(case, activityRule)

    @Test
    @Screenshot(viewId = R.id.screenshot_view, name = "highlight_color_initial")
    fun divScreenshotInitialColor() {
        divFocus { clickOnTopInput() }
    }

    @Test
    @Screenshot(viewId = R.id.screenshot_view, name = "highlight_color_changed")
    fun divScreenshotChangedColor() {
        divInput { clickOnActionButton() }
        divFocus { clickOnTopInput() }
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return AssetEnumerator()
                .enumerate("ui_test_data/input")
                .filter { filename -> filename.endsWith("/div_input_highlight.json") }
                .withEscapedParameter()
        }
    }
}
