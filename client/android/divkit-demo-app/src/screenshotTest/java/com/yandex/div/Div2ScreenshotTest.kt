package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.rule.classRule
import com.yandex.div.rule.screenshotRule
import com.yandex.div.test.SNAPSHOT_TEST_CASES_PATH
import com.yandex.div.test.viewDeviceIgnoredScreenshotCases
import com.yandex.div.test.viewDeviceScreenshotCases
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.screenshot.Screenshot
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class Div2ScreenshotTest(casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)

    @get:Rule
    val rule = screenshotRule(casePath, TEST_CASES_PATH, testCase, activityRule, casePath.relativePath)

    @Test
    @Screenshot
    fun test() = Unit

    companion object {

        const val TEST_CASES_PATH = SNAPSHOT_TEST_CASES_PATH

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)
        private val activityRule = ActivityScenarioRule(DivScreenshotActivity::class.java)

        @JvmField
        @ClassRule
        val classRule = classRule(activityRule)

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val enumerator = AssetEnumerator()
            return enumerator.enumerate(TEST_CASES_PATH)
                .filter { !viewDeviceIgnoredScreenshotCases.contains(it) }
                .filter { it.removePrefix("$TEST_CASES_PATH/") in viewDeviceScreenshotCases }
                .let(enumerator::requireSelectedCase)
                .withEscapedParameter()
        }

        val String.relativePath: String
            get() {
                return substringAfter("$TEST_CASES_PATH${File.separator}")
                    .substringBeforeLast(File.separator)
            }
    }
}
