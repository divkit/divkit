package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.Div2ScreenshotTest.Companion.TEST_CASES_PATH
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.classRule
import com.yandex.div.rule.composeScreenshotRule
import com.yandex.div.test.composeDeviceScreenshotCases
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.screenshot.Screenshot
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DivComposeScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)

    @get:Rule
    val rule = composeScreenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule, casePath.relativePath)

    @Test
    @Screenshot
    fun test() = Unit

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        private val activityRule = ActivityScenarioRule(DivComposeScreenshotActivity::class.java)

        @JvmField
        @ClassRule
        val classRule = classRule(activityRule)

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val enumerator = AssetEnumerator()
            return enumerator.enumerate(TEST_CASES_PATH)
                .filter { composeDeviceScreenshotCases.contains(it) }
                .let(enumerator::requireSelectedCase)
                .withEscapedParameter(prefix = "$TEST_CASES_PATH/")
        }
    }
}
