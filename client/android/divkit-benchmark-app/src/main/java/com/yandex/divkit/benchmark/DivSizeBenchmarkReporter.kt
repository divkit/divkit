package com.yandex.divkit.benchmark

import android.content.Context
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.divkit.regression.utils.AssetReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DivSizeBenchmarkReporter {
    suspend fun report(
        reporter: HistogramReporterDelegate,
        context: Context,
    ) {
        withContext(Dispatchers.IO) {
            val appSize = AssetReader(context)
                .readJson("apk-size/size-report.json")
                .getJSONObject("benchmark-app")
                .getInt("size")
            withContext(Dispatchers.Main) {
                reporter.reportSize("Library.Size.BenchmarkApp", appSize)
            }
        }
    }
}
