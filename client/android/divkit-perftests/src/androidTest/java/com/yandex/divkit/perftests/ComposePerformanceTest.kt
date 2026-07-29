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
        runTest(
            tag = "",
            fileName = "with_templates.json",
            mode = Mode.RECOMPOSITION
        )
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
        runTest(
            tag = "",
            fileName = "with_templates.json",
            mode = Mode.RESET_CONTENT
        )
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
    fun services_recomposition() {
        runTest(
            tag = "services",
            fileName = "services.json",
            mode = Mode.RECOMPOSITION
        )
    }

    private fun runTest(
        tag: String,
        fileName: String,
        mode: Mode
    ) {
        utils.run {
            val tagSuffix = mode.tagSuffix
            report(tag = if (tag.isEmpty()) tagSuffix else "${tag}_$tagSuffix") {
                startActivity(
                    activityClass = "$PACKAGE_NAME.DivComposeBenchmarkActivity",
                    extras = bundleOf(
                        "asset_name" to fileName,
                        "warm_render_mode" to mode.name
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }
        }
    }
}

private enum class Mode {
    RECOMPOSITION,
    RESET_CONTENT;

    val tagSuffix: String
        get() = name.lowercase()
}
