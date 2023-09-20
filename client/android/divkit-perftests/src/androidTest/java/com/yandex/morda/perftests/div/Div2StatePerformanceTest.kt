package com.yandex.morda.perftests.div

import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yandex.divkit.perftests.DIV_STATE_BENCHMARK_ACTIVITY
import com.yandex.divkit.perftests.PACKAGE_NAME
import com.yandex.divkit.perftests.divStateBenchmarkActivityExtras
import com.yandex.divkit.perftests.report
import com.yandex.divkit.perftests.startActivity
import com.yandex.perftests.runner.PerfTest
import com.yandex.perftests.runner.PerfTestParameter
import org.junit.Test

@PerfTest(
    packageName = PACKAGE_NAME,
    description = "Div2 state performance test",
    owners = ["gulevsky"],
    defaultRepeat = 31,
    timeoutSeconds = 3000
)
class Div2StatePerformanceTest : Div2BasePerformanceTest() {
    @PerfTestParameter(
        requiredMetrics = [
            "Div.Parsing.JSON.Cold",
            "Div.Parsing.Total",
            "Div.View.StateSwitching",
            "Div.Parsing.Data.Cold",
            "Div.Parsing.Templates.Cold",
            "Div.Context.Create.Cold",
            "Div.View.Create.Cold",
            "Div.Binding.Cold",
            "Div.Render.Measure.Cold",
            "Div.Render.Layout.Cold",
            "Div.Render.Draw.Cold",
            "Div.Render.Total.Cold",
        ]
    )
    @Test
    fun singleStateChange() {
        utils.run {
            report(packageName = PACKAGE_NAME, tag = "single_state_change") {
                startActivity(
                    packageName = PACKAGE_NAME,
                    activityClass = DIV_STATE_BENCHMARK_ACTIVITY,
                    extras = divStateBenchmarkActivityExtras(
                        assetName = "div2-perf/state-benchmark-single-change.json",
                        statePaths = arrayOf("0/card_content/pager")
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }

            waitAllMetrics(
                "Div.Parsing.JSON.Cold",
                "Div.Parsing.Total",
                "Div.View.StateSwitching",
                "Div.Parsing.Data.Cold",
                "Div.Parsing.Templates.Cold",
                "Div.Context.Create.Cold",
                "Div.View.Create.Cold",
                "Div.Binding.Cold",
                "Div.Render.Measure.Cold",
                "Div.Render.Layout.Cold",
                "Div.Render.Draw.Cold",
                "Div.Render.Total.Cold",
            )
        }
    }

    @PerfTestParameter(
        requiredMetrics = [
            "Div.Parsing.JSON.Cold",
            "Div.Parsing.Total",
            "Div.View.StateSwitching",
            "Div.Parsing.Data.Cold",
            "Div.Parsing.Templates.Cold",
            "Div.Context.Create.Cold",
            "Div.View.Create.Cold",
            "Div.Binding.Cold",
            "Div.Render.Measure.Cold",
            "Div.Render.Layout.Cold",
            "Div.Render.Draw.Cold",
            "Div.Render.Total.Cold",
        ]
    )
    @Test
    fun multipleStateChange() {
        utils.run {
            report(packageName = PACKAGE_NAME, tag = "multiple_state_change") {
                startActivity(
                    packageName = PACKAGE_NAME,
                    activityClass = DIV_STATE_BENCHMARK_ACTIVITY,
                    extras = divStateBenchmarkActivityExtras(
                        assetName = "div2-perf/state-benchmark-multiple-change.json",
                        statePaths = arrayOf(
                            "0/page_01/content",
                            "0/page_02/content",
                            "0/page_03/content",
                            "0/page_04/content",
                            "0/page_05/content",
                            "0/page_06/content",
                            "0/page_07/content",
                            "0/page_08/content"
                        )
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }

            waitAllMetrics(
                "Div.Parsing.JSON.Cold",
                "Div.Parsing.Total",
                "Div.View.StateSwitching",
                "Div.Parsing.Data.Cold",
                "Div.Parsing.Templates.Cold",
                "Div.Context.Create.Cold",
                "Div.View.Create.Cold",
                "Div.Binding.Cold",
                "Div.Render.Measure.Cold",
                "Div.Render.Layout.Cold",
                "Div.Render.Draw.Cold",
                "Div.Render.Total.Cold",
            )
        }
    }
}
