package com.yandex.morda.perftests.div

import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yandex.div.json.getStringOrNull
import com.yandex.divkit.perftests.DIV_FEED_BENCHMARK_ACTIVITY
import com.yandex.divkit.perftests.PACKAGE_NAME
import com.yandex.divkit.perftests.divFeedBenchmarkActivityExtras
import com.yandex.divkit.perftests.pressBack
import com.yandex.divkit.perftests.report
import com.yandex.divkit.perftests.startActivity
import com.yandex.perftests.runner.PerfTest
import com.yandex.perftests.runner.PerfTestJUnit4Runner
import com.yandex.perftests.runner.PerfTestParameter
import com.yandex.perftests.runner.PerfTestUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(PerfTestJUnit4Runner::class)
@PerfTest(
    packageName = PACKAGE_NAME,
    description = "Div2 recycling performance test",
    owners = ["gulevsky"],
    defaultRepeat = 10,
    skipFirstResult = false,
    timeoutSeconds = 600
)
class Div2RecyclingPerformanceTest {

    private val utils = PerfTestUtils(PACKAGE_NAME)

    private var assets = arrayOf("div2-perf/benchmark-with-templates.json")

    @Before
    fun setUp() {
        assets = readCardAssets().ifEmpty { assets }
    }

    @PerfTestParameter(
        requiredMetrics = [
            "Div.Parsing.Data.Cold",
            "Div.Parsing.Data.Warm",
            "Div.Parsing.Templates.Cold",
            "Div.Context.Create.Cold",
            "Div.View.Create.Cold",
            "Div.View.Create.Warm",
            "Div.Binding.Cold",
            "Div.Binding.Warm",
            "Div.Rebinding.Cold",
            "Div.Rebinding.Warm",
            "Div.Render.Measure.Cold",
            "Div.Render.Measure.Warm",
            "Div.Render.Layout.Cold",
            "Div.Render.Layout.Warm",
            "Div.Render.Draw.Cold",
            "Div.Render.Draw.Warm",
            "Div.Render.Total.Cold",
            "Div.Render.Total.Warm",
        ]
    )
    @Test
    fun feedScroll() {
        utils.run {
            report(packageName = PACKAGE_NAME, tag = "feed_scroll") {
                startActivity(
                    packageName = PACKAGE_NAME,
                    activityClass = DIV_FEED_BENCHMARK_ACTIVITY,
                    extras = divFeedBenchmarkActivityExtras(
                        assetNames = assets
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }
            pressBack()
        }
    }

    private fun readCardAssets(): Array<String> {
        val cardAssets = utils.withSettings {
            getStringOrNull(KEY_CARD_ASSETS)
        }
        return if (cardAssets == null) {
            emptyArray()
        } else {
            cardAssets.split(',').toTypedArray()
        }
    }

    private companion object {
        private const val KEY_CARD_ASSETS = "card_assets"
    }
}
