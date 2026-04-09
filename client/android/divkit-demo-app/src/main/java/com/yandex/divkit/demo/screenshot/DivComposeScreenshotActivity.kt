package com.yandex.divkit.demo.screenshot

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.internal.ImageLoaderProvider
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.font.ComposeFontFamilyProvider
import org.json.JSONObject

/**
 * Run:
adb shell am start -n com.yandex.divkit.demo/com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity \
-e DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME snapshot_test_data/div-text/all_attributes.json
 */
@OptIn(InternalApi::class)
class DivComposeScreenshotActivity : ComponentActivity() {

    private lateinit var divContext: DivContext
    private lateinit var data: DivData

    val imageLoadingTracker = ComposeImageLoadingTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageLoaderProvider = ImageLoaderProvider {
            ImageLoader.Builder(context = this)
                .allowHardware(false)
                .eventListener(imageLoadingTracker)
                .build()
        }

        divContext = DivContext(
            baseContext = this,
            configuration = DivComposeConfiguration(
                fontFamilyProvider = ComposeFontFamilyProvider(this)
            ),
            debugConfiguration = DivDebugConfiguration(
                imageLoaderProvider = imageLoaderProvider
            )
        )

        intent.extras?.getString(EXTRA_DIV_ASSET_NAME)?.let {
            setDivData(DivAssetReader(this).read(it))
        }
    }

    fun setDivData(json: JSONObject) {
        val templatesJson = json.optJSONObject("templates")
        val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
            if (templatesJson != null) parseTemplates(templatesJson)
        }
        data = DivData(environment, json.getJSONObject("card"))

        val view = ComposeView(divContext).apply {
            id = R.id.screenshot_view
            setContent {
                DivView(data = data)
            }
        }

        setContentView(
            LinearLayout(divContext).apply {
                addView(view)
            }
        )
    }

    fun performActions(actions: List<DivAction>) {
        actions.forEach {
            divContext.debugFeatures.performAction(data = data, action = it)
        }
    }

    companion object {
        const val EXTRA_DIV_ASSET_NAME = "DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME"
    }
}
