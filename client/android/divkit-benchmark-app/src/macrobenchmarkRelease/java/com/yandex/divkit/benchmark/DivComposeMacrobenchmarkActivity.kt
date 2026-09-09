package com.yandex.divkit.benchmark

import android.os.Bundle
import android.os.Trace
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.trace
import androidx.lifecycle.lifecycleScope
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.DivView
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.regression.utils.AssetReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(29)
class DivComposeMacrobenchmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val assetName = requireNotNull(intent.getStringExtra("asset_name"))
        val root = FrameLayout(this)
        setContentView(root)

        lifecycleScope.launch {
            Trace.beginAsyncSection("Div.Total", TRACE_COOKIE)
            val data = withContext(Dispatchers.IO) {
                val json = trace("Div.ReadJson") {
                    AssetReader(this@DivComposeMacrobenchmarkActivity).readJson(assetName)
                }
                trace("Div.ParseDivData") {
                    val environment = DivParsingEnvironment(MacrobenchmarkReporter).apply {
                        json.optJSONObject("templates")?.let { parseTemplates(it) }
                    }
                    DivData(environment, json.getJSONObject("card"))
                }
            }
            val divContext = trace("Div.CreateContext") {
                DivContext(
                    baseContext = this@DivComposeMacrobenchmarkActivity,
                    configuration = DivConfiguration(
                        histogramConfiguration = object : DivHistogramConfiguration {
                            override val isEnabled = true
                            override val componentName = ""
                            override val histogramBridge = MacrobenchmarkHistogramBridge()
                        },
                        imageLoaderConfiguration = object : ImageLoaderConfiguration {
                            override val reportErrors = false
                        },
                        reporter = MacrobenchmarkReporter
                    )
                )
            }

            Trace.beginAsyncSection("Div.Draw", TRACE_COOKIE)
            val composeView = ComposeView(divContext)
            var firstDraw = true
            composeView.setContent {
                DivView(
                    data = data,
                    modifier = Modifier.safeDrawingPadding().drawWithContent {
                        drawContent()
                        if (firstDraw) {
                            firstDraw = false
                            composeView.post {
                                Trace.endAsyncSection("Div.Draw", TRACE_COOKIE)
                                Trace.endAsyncSection("Div.Total", TRACE_COOKIE)
                                root.contentDescription = "DivView rendered"
                            }
                        }
                    }
                )
            }
            root.addView(composeView)
        }
    }
}

private const val TRACE_COOKIE = 1

private object MacrobenchmarkReporter : DivReporter(), ParsingErrorLogger {
    override fun reportError(message: String) {
        error(message)
    }

    override fun reportError(e: Exception) {
        throw RuntimeException(e)
    }

    override fun logError(e: Exception) {
        reportError(e)
    }
}
