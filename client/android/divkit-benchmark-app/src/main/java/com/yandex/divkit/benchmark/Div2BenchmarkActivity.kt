package com.yandex.divkit.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yandex.divkit.benchmark.div.divContext
import kotlinx.coroutines.launch

class Div2BenchmarkActivity : AppCompatActivity() {

    private lateinit var benchmark: Div2Benchmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = ProfilingLayout(this)
        setContentView(rootLayout)

        val assetName = intent.getStringExtra(EXTRA_ASSET_NAME)
        val rebindAssetName = intent.getStringExtra(EXTRA_REBIND_ASSET_NAME)
        val disableLogs = intent.getBooleanExtra(EXTRA_FORCE_DISABLE_LOGS, false)
        val reportSize = intent.getBooleanExtra(EXTRA_REPORT_SIZE, false)

        val viewController = Div2BenchmarkViewController(rootLayout)
        val divContext = divContext(activity = this, forceDisableLogs = disableLogs)
        benchmark = Div2Benchmark(divContext, viewController)

        if (reportSize) {
            lifecycleScope.launch {
                DivSizeBenchmarkReporter.report(
                    reporter = Container.histogramReporterDelegate,
                    context = this@Div2BenchmarkActivity
                )

                runBenchmark(assetName, viewController, rebindAssetName)
            }
        } else {
            runBenchmark(assetName, viewController, rebindAssetName)
        }
    }

    private fun runBenchmark(
        assetName: String?,
        viewController: Div2BenchmarkViewController,
        rebindAssetName: String?
    ) {
        if (assetName == null) {
            viewController.showMessage("Error: asset file is not specified")
        } else {
            benchmark.run(assetName, rebindAssetName)
        }
    }

    override fun onDestroy() {
        benchmark.cancel()
        super.onDestroy()
    }
}
