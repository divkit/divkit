package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.divFocus
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class Div2FocusScreenshotTest(case: String, escapedCase: String) {

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
    @Screenshot(viewId = R.id.morda_screenshot_div, relativePath = "not_focused")
    fun divScreenshotTopFocused() = Unit

    @Test
    @Screenshot(viewId = R.id.morda_screenshot_div, relativePath = "focused")
    fun divScreenshotBottomFocused() {
        divFocus { clickOnTopInput() }
    }

    companion object {

        private const val TEST_CASES_PATH = "ui_test_data/focus"
        private const val CASE_PREFIX = "snapshot"
        private const val CASE_EXTENSION = ".json"

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases() = AssetEnumerator(context).enumerate(TEST_CASES_PATH) { filename ->
            filename.contains(CASE_PREFIX) && filename.endsWith(CASE_EXTENSION)
        }.withEscapedParameter()
    }
}
