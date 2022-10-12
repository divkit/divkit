package com.yandex.divkit.demo.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.divkit.demo.div.divContext

class Div2BenchmarkActivity : AppCompatActivity() {

    private lateinit var benchmark: Div2Benchmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = ProfilingLayout(this)
        setContentView(rootLayout)

        val assetName = intent.getStringExtra(EXTRA_ASSET_NAME)
        val rebindCount = intent.getIntExtra(EXTRA_REBIND_COUNT, -1)

        val viewController = Div2BenchmarkViewController(rootLayout)
        val divContext = divContext(activity = this)
        benchmark = Div2Benchmark(divContext, viewController, rebindCount)

        if (assetName == null) {
            viewController.showMessage("Error: asset file is not specified")
        } else {
            benchmark.run(assetName)
        }
    }

    override fun onDestroy() {
        benchmark.cancel()
        super.onDestroy()
    }
}
