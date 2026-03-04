package com.yandex.divkit.benchmark

import android.content.Context
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object DivSizeBenchmarkReporter {
    suspend fun report(
        reporter: HistogramReporterDelegate,
        context: Context,
    ) {
        withContext(Dispatchers.IO) {
            val reportJson: String = readAsset(context, "apk-size/size-report.json")
            val report = JSONObject(reportJson)
            val appSize = report.getJSONObject("benchmark-app").getInt("size")
            withContext(Dispatchers.Main) {
                reporter.reportSize("Library.Size.BenchmarkApp", appSize)
            }
        }
    }

    private fun readAsset(context: Context, fileName: String): String {
        val stream = context.assets.open(fileName)
        return stream.use {
            stream.readBytes().toString(charset = Charsets.UTF_8)
        }
    }
}
