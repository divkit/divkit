package com.yandex.divkit.demo.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.divkit.demo.div.DivUtils

class Div2StateBenchmarkActivity : AppCompatActivity() {

    private lateinit var benchmark: Div2StateBenchmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = ProfilingLayout(this)
        setContentView(rootLayout)

        val assetName = intent.getStringExtra(EXTRA_ASSET_NAME)
        val statePaths = intent.getStringArrayExtra(EXTRA_STATE_PATHS)?.toList() ?: emptyList()

        val viewController = Div2BenchmarkViewController(rootLayout)
        val divContext = DivUtils.createDivContext(activity = this)
        benchmark = Div2StateBenchmark(divContext, viewController)

        if (assetName == null) {
            viewController.showMessage("Error: asset file is not specified")
        } else {
            benchmark.run(assetName, statePaths)
        }
    }

    override fun onDestroy() {
        benchmark.cancel()
        super.onDestroy()
    }
}
