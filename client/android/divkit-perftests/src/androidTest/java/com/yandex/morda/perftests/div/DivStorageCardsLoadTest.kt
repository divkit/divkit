package com.yandex.morda.perftests.div

import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yandex.divkit.perftests.divStorageBenchmarkActivityExtras
import com.yandex.divkit.perftests.DIV_STORAGE_BENCHMARK_ACTIVITY
import com.yandex.divkit.perftests.PACKAGE_NAME
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
    description = "Div storage loading test",
    owners = ["edubinskaya", "i-ts", "bgubanov"],
    defaultRepeat = 20,
    timeoutSeconds = 1200
)
class DivStorageCardsLoadTest {

    private val utils = PerfTestUtils(PACKAGE_NAME)

    @Before
    fun setUp() {
        utils.forceStop()
    }

    @PerfTestParameter(
        requiredMetrics = [
            "Storage.Data.Load.Cold",
            "Storage.Templates.Parsing.Cold"
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
                        assetNames = arrayOf("div2-perf/benchmark-with-templates.json",
                            "div2-perf/state-benchmark-multiple-change.json")
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }
        }
    }

    @PerfTestParameter(
        requiredMetrics = [
            "Storage.Data.Load.Cold"
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
                        assetNames = arrayOf("div2-perf/benchmark-without-templates.json",
                            "div2-perf/state-benchmark-multiple-change.json")
                    ),
                    waitCondition = Until.findObject(By.textContains("Finished"))
                )
            }
        }
    }
}
