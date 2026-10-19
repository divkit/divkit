package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.rule.baseScreenshotTestRule
import com.yandex.div.rule.classRule
import com.yandex.div.steps.imageLoaderScreenshot
import com.yandex.div.test.IMAGE_LOADER_SCREENSHOT_CASES_PATH
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_COIL
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity.Companion.IMAGE_LOADER_GLIDE
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.screenshot.Screenshot
import org.junit.After
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Div2ImageLoaderScreenshotTest(private val casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)

    @get:Rule
    val rule = baseScreenshotTestRule(casePath, TEST_CASES_PATH, testCase)

    @Test
    @Screenshot
    fun testGlide() = runTestWith(IMAGE_LOADER_GLIDE)

    @Test
    @Screenshot
    fun testCoil() = runTestWith(IMAGE_LOADER_COIL)

    private fun runTestWith(loaderName: String) {
        imageLoaderScreenshot { runTest(activityRule.scenario, casePath, testCase, loaderName) }
    }

    @After
    fun cleanup() {
        activityRule.scenario.onActivity { it.cleanup() }
    }

    companion object {

        private const val TEST_CASES_PATH = IMAGE_LOADER_SCREENSHOT_CASES_PATH

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)
        private val activityRule = ActivityScenarioRule(DivScreenshotActivity::class.java)

        @JvmField
        @ClassRule
        val classRule = classRule(activityRule)

        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun cases() = AssetEnumerator().enumerate(TEST_CASES_PATH).withEscapedParameter()
    }
}
