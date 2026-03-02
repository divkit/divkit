package com.yandex.divkit.demo.screenshot

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R

/**
 * Run:
adb shell am start -n com.yandex.divkit.demo/com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity \
-e DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME snapshot_test_data/div-text/all_attributes.json
 */
class DivComposeScreenshotActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val divContext = DivComposeConfiguration()
            .createContext(baseContext = this)

        val view = ComposeView(divContext).apply {
            id = R.id.morda_screenshot_div
            setContent {
                DivView(data = parseDivData())
            }
        }

        setContentView(
            LinearLayout(divContext).apply {
                addView(view)
            }
        )
    }

    private fun parseDivData(): DivData {
        val assetName = intent.extras?.getString(EXTRA_DIV_ASSET_NAME)
            ?: throw IllegalArgumentException("Missing div asset name")

        val json = DivAssetReader(this).read(assetName)
        val templatesJson = json.optJSONObject("templates")
        val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
            if (templatesJson != null) parseTemplates(templatesJson)
        }
        return DivData(environment, json.getJSONObject("card"))
    }

    companion object {
        const val EXTRA_DIV_ASSET_NAME = "DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME"
    }
}
