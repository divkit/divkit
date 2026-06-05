package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.DivKit
import com.yandex.div.internal.util.IOUtils
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.integration
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.IntegrationTestCaseParser
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.rules.ActivityParamsTestRule
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class IntegrationMultiplatformTest(testCaseParsingResult: ParsingResult<IntegrationTestCase>) {

    val activityRule = ActivityParamsTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    private val testCase = testCaseParsingResult.getOrThrow()

    @Test
    fun run() {
        integration(testCase, activityRule.activity) {
            checkResult()
        }
    }

    @After
    fun tearDown() = DivKit.getInstance(context).reset()

    companion object {

        private val context: Context get() = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameters(name = "{0}")
        fun cases(): List<ParsingResult<IntegrationTestCase>> {
            return AssetEnumerator(context).enumerate("integration_test_data").flatMap {
                IntegrationTestCaseParser.parseCases(it, IOUtils.toString(context.assets.open(it)))
            }
        }
    }
}
