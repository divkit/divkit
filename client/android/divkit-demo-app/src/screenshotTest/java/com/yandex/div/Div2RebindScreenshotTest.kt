package com.yandex.div

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.Div2ScreenshotTest.Companion.PRODUCTION_CASES_PATH
import com.yandex.div.Div2ScreenshotTest.Companion.TEST_CASES_PATH
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.ActivityIdlingResource
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.DIV_SCREENSHOT_CASE_EXTENSION
import com.yandex.test.screenshot.ReferenceFileWriter
import com.yandex.test.screenshot.Screenshot
import com.yandex.test.screenshot.ScreenshotType
import com.yandex.test.screenshot.caseName
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class Div2RebindScreenshotTest(private val case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivScreenshotActivity::class.java,
        DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @Rule
    @JvmField
    val rule = screenshotRule(case, activityRule, case.relativePath)

    @Screenshot(viewId = R.id.morda_screenshot_div)
    @Test
    fun divScreenshot() {
        context.sendBroadcastAndWait<DivScreenshotActivity>(DivScreenshotActivity.REBIND_DIV_WITH_SAME_DATA_ACTION)
        createReferenceOverride()
    }

    private fun createReferenceOverride() {
        val caseRelativePath = case.relativePath
        val caseName = case.caseName
        ScreenshotType.values().forEach {
            val targetSuiteName = "${Div2RebindScreenshotTest::class.qualifiedName}/$caseRelativePath"
            val actualSuiteName = "${Div2ScreenshotTest::class.qualifiedName}/$caseRelativePath"

            ReferenceFileWriter.append(
                it.relativeScreenshotPath(targetSuiteName, caseName),
                it.relativeScreenshotPath(actualSuiteName, caseName)
            )
        }
    }

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val filter = { filename: String ->
                filename.endsWith(DIV_SCREENSHOT_CASE_EXTENSION) && !filename.contains("templates")
            }
            val testCases = AssetEnumerator(context).enumerate(TEST_CASES_PATH, filter)
            val productionCases = AssetEnumerator(context).enumerate(PRODUCTION_CASES_PATH, filter)
            return (testCases + productionCases).withEscapedParameter()
        }
    }
}

inline fun <reified T: Activity> Context.sendBroadcastAndWait(
    action: String
) {
    val intent = Intent(action)

    sendBroadcast(intent)
    waitForIdlingResource(ActivityIdlingResource(T::class.java))
}
