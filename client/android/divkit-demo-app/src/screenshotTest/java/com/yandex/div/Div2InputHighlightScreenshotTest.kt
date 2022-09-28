package com.yandex.div

import com.yandex.div.steps.divFocus
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test

private const val CASE_PATH = "ui_test_data/input/div_input_highlight.json"

class Div2InputHighlightScreenshotTest {

    private val activityRule = com.yandex.div.rule.ActivityParamsTestRule(
            DivScreenshotActivity::class.java,
            DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to CASE_PATH
    )

    @get:Rule
    val rule = screenshotRule(casePath = CASE_PATH) { activityRule }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, name = "highlight_color_initial")
    fun divScreenshotInitialColor() {
        divFocus { clickOnBottomInput() }
    }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, name = "highlight_color_changed")
    fun divScreenshotChangedColor() {
        divFocus { clickOnBottomInput() }
        divInput { clickOnActionButton() }
    }
}
