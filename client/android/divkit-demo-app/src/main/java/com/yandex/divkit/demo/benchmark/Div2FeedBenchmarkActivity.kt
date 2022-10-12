package com.yandex.divkit.demo.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.divkit.demo.div.divContext

class Div2FeedBenchmarkActivity : AppCompatActivity() {

    private lateinit var benchmark: Div2FeedBenchmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@Div2FeedBenchmarkActivity)
        }
        setContentView(recyclerView)

        val assetNames = intent.getStringArrayExtra(EXTRA_ASSET_NAMES)

        val viewController = Div2FeedBenchmarkViewController(recyclerView)
        val divContext = divContext(activity = this)
        benchmark = Div2FeedBenchmark(divContext, viewController)

        if (assetNames == null) {
            viewController.showMessage("Error: asset files are not specified")
        } else {
            benchmark.run(assetNames)
        }
    }

    override fun onDestroy() {
        benchmark.cancel()
        super.onDestroy()
    }
}
