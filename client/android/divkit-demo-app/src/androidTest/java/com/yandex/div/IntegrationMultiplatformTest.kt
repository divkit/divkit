package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.core.DivKit
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.integration
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.IntegrationTestCaseParser
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.regression.utils.AssetReader
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class IntegrationMultiplatformTest(testCaseParsingResult: ParsingResult<IntegrationTestCase>) {

    val activityRule = ActivityScenarioRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    private val testCase = testCaseParsingResult.getOrThrow()

    @Test
    fun run() {
        integration(testCase, activityRule.scenario) {
            checkResult()
        }
    }

    @After
    fun tearDown() = DivKit.getInstance(context).reset()

    companion object {

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameters(name = "{0}")
        fun cases(): List<ParsingResult<IntegrationTestCase>> {
            return AssetEnumerator()
                .enumerate("integration_test_data")
                .flatMap { fileName ->
                    IntegrationTestCaseParser.parseCases(
                        fileName = fileName,
                        json = AssetReader(context).readJson(fileName)
                    )
                }
        }
    }
}
