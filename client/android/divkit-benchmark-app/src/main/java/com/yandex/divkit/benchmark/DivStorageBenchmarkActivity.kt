package com.yandex.divkit.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.divkit.benchmark.div.divContext

class DivStorageBenchmarkActivity : AppCompatActivity() {

    private lateinit var benchmark: DivStorageBenchmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = ProfilingLayout(this@DivStorageBenchmarkActivity)
        setContentView(rootLayout)

        val assetNames = intent.getStringArrayExtra(EXTRA_ASSET_NAMES)
        val prohibitedHistograms = intent.getStringArrayExtra(EXTRA_PROHIBITED_HISTOGRAMS)

        val viewController = DivStorageBenchmarkViewController(rootLayout)
        val divContext = divContext(activity = this)
        benchmark = DivStorageBenchmark(divContext, this, viewController, prohibitedHistograms)

        if (assetNames == null || assetNames.isEmpty()) {
            viewController.showMessage("Error: asset file is not specified")
        } else {
            benchmark.run(assetNames)
        }
    }
}
