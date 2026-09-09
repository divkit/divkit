package com.yandex.divkit.macrobenchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ComposeMacrobenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun withTemplates() = firstRender("with_templates.json")

    @Test
    fun services() = firstRender("services.json")

    @OptIn(ExperimentalMetricApi::class)
    private fun firstRender(assetName: String) {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                StartupTimingMetric(),
                FrameTimingMetric(),
                TraceSectionMetric("Div.ReadJson", TraceSectionMetric.Mode.First),
                TraceSectionMetric("Div.ParseDivData", TraceSectionMetric.Mode.First),
                TraceSectionMetric("Div.CreateContext", TraceSectionMetric.Mode.First),
                TraceSectionMetric("Div.Draw", TraceSectionMetric.Mode.First),
                TraceSectionMetric("Div.Total", TraceSectionMetric.Mode.First),
                HistogramMetric("Div.Composition"),
                HistogramMetric("Div.RenderEffects"),
            ),
            compilationMode = CompilationMode.Full(),
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = { pressHome() }
        ) {
            startActivityAndWait(
                Intent().apply {
                    setClassName(PACKAGE_NAME, "com.yandex.divkit.benchmark.DivComposeMacrobenchmarkActivity")
                    putExtra("asset_name", assetName)
                }
            )
            assertTrue(
                "The card $assetName did not render within 15 seconds",
                device.wait(Until.hasObject(By.desc("DivView rendered")), 15_000)
            )
        }
    }
}

private const val PACKAGE_NAME = "com.yandex.divkit.benchmark.macrobenchmark"
