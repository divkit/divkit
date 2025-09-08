package com.yandex.morda.perftests.div

import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yandex.divkit.perftests.DIV_BENCHMARK_ACTIVITY
import com.yandex.divkit.perftests.PACKAGE_NAME
import com.yandex.divkit.perftests.divBenchmarkActivityRebindExtras
import com.yandex.divkit.perftests.report
import com.yandex.divkit.perftests.startActivity
import com.yandex.perftests.runner.PerfTest
import com.yandex.perftests.runner.PerfTestParameter
import org.junit.Before
import org.junit.Test

@PerfTest(
    packageName = PACKAGE_NAME,
    description = "Div2 rebinding performance test",
    owners = ["the-leo"],
    defaultRepeat = 50,
    timeoutSeconds = 3000
)
class Div2RebindPerformanceTest : Div2BasePerformanceTest() {
    @Before
    fun setUp() {
        utils.forceStop()
    }

    @PerfTestParameter(
        importantMetrics = [
            "Div.Binding.Cold",
            "Div.Parsing.Data.Cold",
            "Div.Parsing.Data.Warm",
            "Div.Parsing.JSON.Cold",
            //"Div.Parsing.JSON.Warm", // Generates unstable results at some perf-tests.
            "Div.Parsing.Total.Cold",
            "Div.Parsing.Total.Warm",
            "Div.Render.Total.Cold",
            "Div.Render.Total.Warm",
            //"Div.Rebinding.Cold", // Generates unstable results at some perf-tests.
            "Div.Rebinding.Warm"
        ]
    )
    @Test
    fun rebindWithMultipleStructureChanges() {
        doRebindTest(
            tag = "rebind_multiple_structure_changes",
            rebindAsset = "div2-perf/rebind-multiple-structure-changes.json"
        )
    }

    @PerfTestParameter(
        importantMetrics = [
            "Div.Binding.Cold",
            "Div.Parsing.Data.Cold",
            "Div.Parsing.Data.Warm",
            "Div.Parsing.JSON.Cold",
            "Div.Parsing.JSON.Warm",
            "Div.Parsing.Total.Cold",
            "Div.Parsing.Total.Warm",
            "Div.Render.Total.Cold",
            //"Div.Render.Total.Warm", // Generates unstable results at some perf-tests.
            "Div.Rebinding.Cold",
            "Div.Rebinding.Warm"
        ]
    )
    @Test
    fun rebindWithSingleStructureChange() {
        doRebindTest(
            tag = "rebind_single_structure_change",
            rebindAsset = "div2-perf/rebind-single-structure-change.json"
        )
    }

    @PerfTestParameter(
        importantMetrics = [
            "Div.Binding.Cold",
            "Div.Parsing.Data.Cold",
            "Div.Parsing.Data.Warm",
            "Div.Parsing.JSON.Cold",
            "Div.Parsing.JSON.Warm",
            "Div.Parsing.Total.Cold",
            "Div.Parsing.Total.Warm",
            "Div.Render.Total.Cold",
            //"Div.Render.Total.Warm", // Generates unstable results at some perf-tests.
            //"Div.Rebinding.Cold", // Generates unstable results at some perf-tests.
            "Div.Rebinding.Warm"
        ]
    )
    @Test
    fun rebindWithNoStructureChanges() {
        doRebindTest(
            tag = "rebind_no_structure_changes",
            rebindAsset = "div2-perf/rebind-no-structure-changes.json"
        )
    }

    private fun doRebindTest(tag: String, rebindAsset: String) {
        utils.run {
            report(packageName = PACKAGE_NAME, tag = tag) {
                startActivity(
                    packageName = PACKAGE_NAME,
                    activityClass = DIV_BENCHMARK_ACTIVITY,
                    extras = divBenchmarkActivityRebindExtras(
                        assetName = "div2-perf/rebind-original-layout.json",
                        rebindAssetName = rebindAsset
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }

            waitAllMetrics(
                "Div.Parsing.JSON.Cold",
                "Div.Parsing.JSON.Warm",
                "Div.Parsing.Total.Cold",
                "Div.Parsing.Total.Warm",
                "Div.Parsing.Data.Cold",
                "Div.Parsing.Data.Warm",
                "Div.Parsing.Templates.Cold",
                "Div.Parsing.Templates.Warm",
                "Div.Context.Create.Cold",
                "Div.View.Create.Cold",
                "Div.View.Create.Warm",
                "Div.Binding.Cold",
                "Div.Binding.Warm",
                "Div.Render.Measure.Cold",
                "Div.Render.Measure.Warm",
                "Div.Render.Layout.Cold",
                "Div.Render.Layout.Warm",
                "Div.Render.Draw.Cold",
                "Div.Render.Draw.Warm",
                "Div.Render.Total.Cold",
                "Div.Render.Total.Warm",
                "Div.Rebinding.Cold",
                "Div.Rebinding.Warm"
            )
        }
    }
}
