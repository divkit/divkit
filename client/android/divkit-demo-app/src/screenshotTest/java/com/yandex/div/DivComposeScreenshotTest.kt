package com.yandex.div

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.DIV_SCREENSHOT_CASE_EXTENSION
import com.yandex.test.screenshot.Screenshot
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DivComposeScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivComposeScreenshotActivity::class.java,
        DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @Rule
    @JvmField
    val rule = screenshotRule(case, activityRule, case.relativePath)

    @Screenshot(viewId = R.id.screenshot_view)
    @Test
    fun divScreenshot() {
        Assume.assumeTrue(
            "Skipping DivComposeScreenshotTest on API 24",
            Build.VERSION.SDK_INT > Build.VERSION_CODES.N
        )
    }

    companion object {
        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return listOf(
                // div-separator
                "snapshot_test_data/div-separator",
                // div-text
                "snapshot_test_data/div-text/all_attributes.json",
                "snapshot_test_data/div-text/variables-rendering.json",
                "snapshot_test_data/div-text/visibility-gone.json",
                "snapshot_test_data/div-text/visibility-invisible.json",
                // div-container
                "snapshot_test_data/div-container",
                "snapshot_test_data/div-container/constraint-propagation",
            ).expandDirectories().withEscapedParameter()
        }

        private fun List<String>.expandDirectories(): List<String> {
            return flatMap { path ->
                if (path.endsWith(DIV_SCREENSHOT_CASE_EXTENSION)) {
                    listOf(path)
                } else {
                    AssetEnumerator(context).enumerate(path, predicate = { filename ->
                        filename.endsWith(DIV_SCREENSHOT_CASE_EXTENSION)
                    }, recursive = false)
                }
            }
        }
    }
}
