package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.Div2ScreenshotTest.Companion.TEST_CASES_PATH
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class Div2RebindScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)
    private val activityRule = ActivityParamsTestRule(DivScreenshotActivity::class.java)

    @Rule
    @JvmField
    val rule = screenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule, casePath.relativePath, expectedSuite)

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activityRule.activity.setDivData(testCase)
        }
    }

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        private val expectedSuite = Div2ScreenshotTest::class.qualifiedName ?: ""

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases() = Div2ScreenshotTest.cases()
    }
}
