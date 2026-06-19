package com.yandex.divkit.perftests

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
            "DivCompose.Render.Total.Cold",
            "DivCompose.Render.Total.Warm",
        ]
    )
    @Test
    fun withTemplates() {
        utils.run {
            report(tag = "with_templates") {
                startActivity(
                    activityClass = "$PACKAGE_NAME.DivComposeBenchmarkActivity",
                    extras = divBenchmarkActivityExtras(assetName = "with_templates.json")
                )
            }

            waitAllMetrics(
                "DivCompose.Render.Composition.Cold",
                "DivCompose.Render.Composition.Warm",
                "DivCompose.Render.Total.Cold",
                "DivCompose.Render.Total.Warm",
            )
        }
    }
}
