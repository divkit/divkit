package com.yandex.divkit.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.DivView
import com.yandex.div.compose.histogram.DivHistogramConfiguration
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.benchmark.div.histogram.LoggingHistogramBridge
import com.yandex.divkit.benchmark.utils.JsonAssetReader

/**
 * Start activity with default data:
 * adb shell am start -n com.yandex.divkit.benchmark/.DivComposeBenchmarkActivity --es asset_name with_templates.json
 */
class DivComposeBenchmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = parseDivData()
        val composeView = ComposeView(createDivContext()).apply {
            setContent {
                DivView(data)
            }
        }

        val rootLayout = ProfilingLayout(this).apply {
            addView(composeView)
        }
        setContentView(rootLayout)
    }

    private fun parseDivData(): DivData {
        val assetName = intent.getStringExtra(EXTRA_ASSET_NAME)
            ?: throw RuntimeException("Extra is required: $EXTRA_ASSET_NAME")

        val json = JsonAssetReader(this).readJson(assetName)
        val templatesJson = json.optJSONObject("templates")
        val environment = DivParsingEnvironment(FailingReporter).apply {
            if (templatesJson != null) parseTemplates(templatesJson)
        }
        return DivData(environment, json.getJSONObject("card"))
    }

    private fun createDivContext(): DivContext {
        return DivContext(
            baseContext = this,
            configuration = DivComposeConfiguration(
                histogramConfiguration = object : DivHistogramConfiguration {
                    override val isEnabled = true
                    override val componentName = ""
                    override val histogramBridge = LoggingHistogramBridge()
                },
                reporter = FailingReporter
            )
        )
    }
}

private object FailingReporter : DivReporter(), ParsingErrorLogger {
    override fun reportError(message: String) {
        throw RuntimeException(message)
    }

    override fun reportError(e: Exception) {
        throw RuntimeException(e.message)
    }

    override fun logError(e: Exception) {
        reportError(e)
    }
}
