package com.yandex.divkit.demo.screenshot

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivFontFamilyProvider
import com.yandex.div.compose.DivView
import com.yandex.div.compose.internal.ImageLoaderProvider
import com.yandex.div.compose.createContext
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.font.RobotoFlexTypefaceProvider
import com.yandex.divkit.demo.font.YandexSansCondensedTypefaceProvider
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

        val imageLoaderProvider = ImageLoaderProvider { context ->
            ImageLoader.Builder(context = context)
                .allowHardware(false)
                .eventListener(imageLoadingTracker)
                .build()
        }

        val defaultProvider = YandexSansDivTypefaceProvider(this)
        val additionalProviders = mapOf(
            "display" to YandexSansDisplayDivTypefaceProvider(this) as DivTypefaceProvider,
            "condensed" to YandexSansCondensedTypefaceProvider(this) as DivTypefaceProvider,
            "roboto_flex" to RobotoFlexTypefaceProvider(this) as DivTypefaceProvider,
        )
        val fontFamilyProvider = TypefaceProviderFontFamilyAdapter(defaultProvider, additionalProviders)

        divContext = DivComposeConfiguration(
            debugConfiguration = DivDebugConfiguration(
                imageLoaderProvider = imageLoaderProvider
            ),
            fontFamilyProvider = fontFamilyProvider,
        ).createContext(baseContext = this)

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

private class TypefaceProviderFontFamilyAdapter(
    private val defaultProvider: DivTypefaceProvider,
    private val additionalProviders: Map<String, DivTypefaceProvider>,
) : DivFontFamilyProvider {

    override fun getFontFamily(fontFamilyName: String?, weight: FontWeight): FontFamily {
        val provider = fontFamilyName?.let { additionalProviders[it] } ?: defaultProvider
        val typeface = provider.getTypefaceFor(weight.weight) ?: return FontFamily.Default
        return FontFamily(typeface)
    }
}
