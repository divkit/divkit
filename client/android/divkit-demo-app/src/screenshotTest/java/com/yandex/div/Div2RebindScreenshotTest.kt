package com.yandex.div

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Assume
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
    val rule = screenshotRule(case, activityRule, case.relativePath, expectedSuite)

    @Screenshot(viewId = R.id.screenshot_view)
    @Test
    fun divScreenshot() {
        Assume.assumeTrue(
            "Skipping Div2RebindScreenshotTest on API 24",
            Build.VERSION.SDK_INT > Build.VERSION_CODES.N
        )
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activityRule.activity.setDivData(case)
        }
    }

    companion object {

        private val expectedSuite = Div2ScreenshotTest::class.qualifiedName ?: ""
        private val ignoredCases = listOf(
            "snapshot_test_data/div-container/item_builder/item-builder-with-local-variables.json",
            "snapshot_test_data/div-container/item_builder/item-builder-with-nested-local-variables.json",
            "snapshot_test_data/image-formats/svg/svg_preview_url_in_gif_image.json",
            "snapshot_test_data/image-formats/svg/svg_preview_url_in_gif_image_scale.json",
        )

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases() = Div2ScreenshotTest.cases().filter { !ignoredCases.contains(it[0]) }
    }
}
