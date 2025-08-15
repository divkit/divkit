package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.ActivityParamsTestRule
import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.divFocus
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class Div2InputHighlightScreenshotTest(case: String, escapedCase: String) {

    private val caseName = case
        .substringAfterLast(File.separator)
        .substringBeforeLast(CASE_EXTENSION)

    private val activityRule = ActivityParamsTestRule(
            DivScreenshotActivity::class.java,
            DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @get:Rule
    val rule = screenshotRule(name = caseName, casePath = case) { activityRule }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, name = "highlight_color_initial")
    fun divScreenshotInitialColor() {
        divFocus { clickOnTopInput() }
    }

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, name = "highlight_color_changed")
    fun divScreenshotChangedColor() {
        divInput { clickOnActionButton() }
        divFocus { clickOnTopInput() }
    }

    companion object {
        private const val TEST_CASES_PATH = "ui_test_data/input"
        private const val CASE_EXTENSION = ".json"

        private const val CASE_NAME = "/div_input_highlight.json"

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases() = AssetEnumerator(context).enumerate(TEST_CASES_PATH) { filename ->
            filename.endsWith(CASE_NAME)
        }.withEscapedParameter()
    }
}
