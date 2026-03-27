package com.yandex.div

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.idling.ActivityIdlingResource
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
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
        context.sendBroadcastAndWait<DivScreenshotActivity>(DivScreenshotActivity.REBIND_DIV_WITH_SAME_DATA_ACTION)
    }

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val expectedSuite = Div2ScreenshotTest::class.qualifiedName ?: ""

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases() = Div2ScreenshotTest.cases()
    }
}

inline fun <reified T: Activity> Context.sendBroadcastAndWait(
    action: String
) {
    val intent = Intent(action)

    sendBroadcast(intent)
    waitForIdlingResource(ActivityIdlingResource(T::class.java))
}
