package com.yandex.divkit.benchmark

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Start activity with default data:
 * adb shell am start -n com.yandex.divkit.benchmark/.DivComposeBenchmarkActivity --es asset_name with_templates.json --es warm_render_mode RESET_CONTENT
 */
@SuppressLint("SetTextI18n")
class DivComposeBenchmarkActivity : AppCompatActivity() {
    private val histogramBridge = LoggingHistogramBridge()

    private lateinit var divContext: DivContext
    private lateinit var composeView: ComposeView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = FrameLayout(this)
        setContentView(rootLayout)

        textView = TextView(this).apply {
            textSize = 24f
            gravity = Gravity.CENTER
            rootLayout.addView(this)
        }

        divContext = createDivContext()
        composeView = ComposeView(divContext).apply {
            rootLayout.addView(this)
        }

        lifecycleScope.launch {
            runBenchmark()
        }
    }

    private suspend fun runBenchmark() = coroutineScope {
        showMessage("Parsing DivData...")

        val data = withContext(Dispatchers.IO) {
            parseDivData()
        }

        showMessage("Rendering cold DivView...")

        composeView.setContent {
            DivView(
                data = data,
                modifier = Modifier.safeDrawingPadding()
            )
        }

        waitHistogram("DivCompose.Render.Total.Cold")

        val modeString = intent.getStringExtra("warm_render_mode")
            ?: throw RuntimeException("Extra is required: warm_render_mode")
        when (WarmRenderMode.valueOf(modeString)) {
            WarmRenderMode.RECOMPOSITION -> {
                divContext.debugFeatures.forceRecomposition(data)
            }

            WarmRenderMode.RESET_CONTENT -> {
                showMessage("Rendering warm DivView...")

                composeView.setContent {
                    DivView(
                        data = data,
                        modifier = Modifier.safeDrawingPadding()
                    )
                }
            }
        }

        waitHistogram("DivCompose.Render.Total.Warm")

        showMessage("Finished")
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
                    override val histogramBridge = this@DivComposeBenchmarkActivity.histogramBridge
                },
                reporter = FailingReporter
            )
        )
    }

    private suspend fun showMessage(message: String) {
        composeView.setContent { }
        textView.text = message
        delay(100.milliseconds)
    }

    private suspend fun waitHistogram(name: String) {
        withTimeout(15.seconds) {
            histogramBridge.histograms.first { it == name }
        }
    }
}

private enum class WarmRenderMode {
    RECOMPOSITION,
    RESET_CONTENT
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
