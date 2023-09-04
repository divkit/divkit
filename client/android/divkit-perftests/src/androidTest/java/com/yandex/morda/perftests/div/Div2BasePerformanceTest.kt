package com.yandex.morda.perftests.div

import com.yandex.divkit.perftests.PACKAGE_NAME
import com.yandex.divkit.perftests.reset
import com.yandex.divkit.perftests.resetAndCompile
import com.yandex.perftests.runner.PerfTestAfterClass
import com.yandex.perftests.runner.PerfTestBeforeClass
import com.yandex.perftests.runner.PerfTestJUnit4Runner
import com.yandex.perftests.runner.PerfTestUtils
import org.junit.runner.RunWith

@RunWith(PerfTestJUnit4Runner::class)
abstract class Div2BasePerformanceTest {
    protected val utils = PerfTestUtils(PACKAGE_NAME)

    @PerfTestBeforeClass
    fun setUpTestClass() {
        utils.resetAndCompile(PACKAGE_NAME)
    }

    @PerfTestAfterClass
    fun tearDownClass() {
        utils.reset(PACKAGE_NAME)
    }
}
