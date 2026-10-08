package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.Div2ScreenshotTest.Companion.TEST_CASES_PATH
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.classRule
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.screenshot.Screenshot
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class Div2RebindScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)

    @get:Rule
    val rule = screenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule, casePath.relativePath, expectedSuite)

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() {
        activityRule.scenario.onActivity { it.setDivData(testCase) }
    }

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)
        private val activityRule = ActivityScenarioRule(DivScreenshotActivity::class.java)

        private val expectedSuite = Div2ScreenshotTest::class.qualifiedName ?: ""

        @JvmField
        @ClassRule
        val classRule = classRule(activityRule)

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases() = Div2ScreenshotTest.cases()
    }
}
