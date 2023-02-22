package com.yandex.divkit.demo.benchmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yandex.divkit.demo.div.divContext

class DivStorageBenchmarkActivity : AppCompatActivity() {

    private lateinit var benchmark: DivStorageBenchmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = ProfilingLayout(this@DivStorageBenchmarkActivity)
        setContentView(rootLayout)

        val assetNames = intent.getStringArrayExtra(EXTRA_ASSET_NAMES)

        val viewController = DivStorageBenchmarkViewController(rootLayout)
        val divContext = divContext(activity = this)
        benchmark = DivStorageBenchmark(divContext, this, viewController)

        if (assetNames == null || assetNames.isEmpty()) {
            viewController.showMessage("Error: asset file is not specified")
        } else {
            benchmark.run(assetNames)
        }
    }
}
