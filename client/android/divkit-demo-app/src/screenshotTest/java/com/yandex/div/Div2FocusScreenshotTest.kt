package com.yandex.div

import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.divFocus
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Div2FocusScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivScreenshotActivity::class.java,
        DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @get:Rule
    val rule = screenshotRule(case, activityRule)

    @Screenshot(
        viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG,
        relativePath = "not_focused"
    )
    @Test
    fun divScreenshotNotFocused() = Unit

    @Screenshot(
        viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG,
        relativePath = "focused"
    )
    @Test
    fun divScreenshotFocused() {
        divFocus { clickOnTopInput() }
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return AssetEnumerator()
                .enumerate("ui_test_data/focus")
                .filter { filename -> filename.contains("snapshot") }
                .withEscapedParameter()
        }
    }
}
