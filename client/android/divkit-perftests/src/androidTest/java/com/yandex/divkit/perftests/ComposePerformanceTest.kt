package com.yandex.divkit.perftests

import androidx.core.os.bundleOf
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yandex.perftests.runner.PerfTest
import com.yandex.perftests.runner.PerfTestParameter
import org.junit.Before
import org.junit.Test

@PerfTest(
    packageName = PACKAGE_NAME,
    description = "DivKit Compose performance test",
    owners = ["pkurchatov"],
    defaultRepeat = 50,
    timeoutSeconds = 3000
)
class ComposePerformanceTest : BasePerformanceTest() {

    @Before
    fun setUp() {
        utils.forceStop()
    }

    @PerfTestParameter(
        importantMetrics = [
            "DivCompose.Render.Composition.Cold",
            "DivCompose.Render.Composition.Warm",
            "DivCompose.Render.Effects.Cold",
            "DivCompose.Render.Effects.Warm",
            "DivCompose.Render.Total.Cold",
            "DivCompose.Render.Total.Warm",
        ]
    )
    @Test
    fun recomposition() {
        runTest(tag = "recomposition", mode = "RECOMPOSITION")
    }

    @PerfTestParameter(
        importantMetrics = [
            "DivCompose.Render.Composition.Cold",
            "DivCompose.Render.Composition.Warm",
            "DivCompose.Render.Effects.Cold",
            "DivCompose.Render.Effects.Warm",
            "DivCompose.Render.Total.Cold",
            "DivCompose.Render.Total.Warm",
        ]
    )
    @Test
    fun resetContent() {
        runTest(tag = "reset_content", mode = "RESET_CONTENT")
    }

    private fun runTest(tag: String, mode: String) {
        utils.run {
            report(tag = tag) {
                startActivity(
                    activityClass = "$PACKAGE_NAME.DivComposeBenchmarkActivity",
                    extras = bundleOf(
                        "asset_name" to "with_templates.json",
                        "warm_render_mode" to mode
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }
        }
    }
}
