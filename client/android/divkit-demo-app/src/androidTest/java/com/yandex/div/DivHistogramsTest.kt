package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.HistogramsType
import com.yandex.div.steps.TestHistogramDispatcher
import com.yandex.div.steps.divHistograms
import com.yandex.divkit.demo.benchmark.Div2BenchmarkActivity
import com.yandex.divkit.demo.div.histogram.LoggingHistogramBridge
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DivHistogramsTest {

    private val activityRule = ActivityTestRule(Div2BenchmarkActivity::class.java, false, false)
    private val dispatcher = TestHistogramDispatcher()

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Before
    fun setUpDispatcher() {
        LoggingHistogramBridge.dispatcher = dispatcher
    }

    @After
    fun clearDispatcher() {
        LoggingHistogramBridge.dispatcher = null
    }

    @Test
    fun collectDivHistograms() {
        HistogramsType.values().forEach { histogramsType ->
            divHistograms {
                activityRule.collectHistograms(dispatcher, histogramsType)
            } assert {
                checkCollectedHistograms(dispatcher, histogramsType)
            }
        }
    }
}
