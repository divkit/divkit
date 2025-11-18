package com.yandex.morda.perftests.div

import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yandex.divkit.perftests.DIV_STORAGE_BENCHMARK_ACTIVITY
import com.yandex.divkit.perftests.PACKAGE_NAME
import com.yandex.divkit.perftests.divStorageBenchmarkActivityExtras
import com.yandex.divkit.perftests.report
import com.yandex.divkit.perftests.startActivity
import com.yandex.perftests.runner.PerfTest
import com.yandex.perftests.runner.PerfTestParameter
import org.junit.Before
import org.junit.Test

@PerfTest(
    packageName = PACKAGE_NAME,
    description = "Div storage loading test",
    owners = ["edubinskaya", "i-ts", "bgubanov"],
    defaultRepeat = 50,
    timeoutSeconds = 3000
)
class DivStorageCardsLoadTest: Div2BasePerformanceTest() {
    @Before
    fun setUp() {
        utils.forceStop()
    }

    @PerfTestParameter(
        importantMetrics = [
            //"Storage.Data.Load.Cold", // Generates unstable results at some perf-tests.
            "Storage.Templates.Parsing.Cold",
        ]
    )
    @Test
    fun withTemplates() {
        utils.run {
            report(packageName = PACKAGE_NAME, tag = "with_templates") {
                startActivity(
                    packageName = PACKAGE_NAME,
                    activityClass = DIV_STORAGE_BENCHMARK_ACTIVITY,
                    extras = divStorageBenchmarkActivityExtras(
                        assetNames = arrayOf("perf_test_data/with_templates.json",
                            "div2-perf/state-benchmark-multiple-change.json"),
                        prohibitedHistograms = arrayOf("Storage.Templates.Load",
                            "com.yandex.divkit.demo.Storage.Templates.Parsing",
                            "com.yandex.divkit.demo.Storage.Data.Load",
                            "com.yandex.divkit.demo.Storage.Templates.Load"),
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }

            waitAllMetrics(
                "Storage.Data.Load.Cold",
                "Storage.Templates.Parsing.Cold",
            )
        }
    }

    @PerfTestParameter(
        importantMetrics = [
            "Storage.Data.Load.Cold",
        ]
    )
    @Test
    fun withoutTemplates() {
        utils.run {
            report(packageName = PACKAGE_NAME, tag = "without_templates") {
                startActivity(
                    packageName = PACKAGE_NAME,
                    activityClass = DIV_STORAGE_BENCHMARK_ACTIVITY,
                    extras = divStorageBenchmarkActivityExtras(
                        assetNames = arrayOf("perf_test_data/without_templates.json",
                            "div2-perf/state-benchmark-multiple-change.json"),
                        prohibitedHistograms = arrayOf("Storage.Templates.Load",
                            "Storage.Templates.Parsing",
                            "com.yandex.divkit.demo.Storage.Data.Load",
                            "com.yandex.divkit.demo.Storage.Templates.Parsing",
                            "com.yandex.divkit.demo.Storage.Templates.Load",
                        )
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }

            waitAllMetrics(
                "Storage.Data.Load.Cold",
            )
        }
    }
}
