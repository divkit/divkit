package com.yandex.divkit.demo.screenshot

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.allowHardware
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.images.DivkitAssetUriMapper
import com.yandex.div.compose.images.gifDecoderFactory
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.internal.ImageLoaderProvider
import com.yandex.div.compose.video.viewbased.ViewBasedDivVideoPlayerFactory
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.video.m3.ExoDivPlayerFactory
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.font.ComposeFontSourceProvider
import okio.ByteString.Companion.encodeUtf8
import org.json.JSONObject

/**
 * Run:
adb shell am start -n com.yandex.divkit.demo/com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity \
-e DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME snapshot_test_data/div-text/all_attributes.json
 */
@OptIn(InternalApi::class, ExperimentalApi::class)
class DivComposeScreenshotActivity : ComponentActivity() {

    private lateinit var divContext: DivContext
    private lateinit var data: DivData

    val imageLoadingTracker = ComposeImageLoadingTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageLoaderProvider = ImageLoaderProvider {
            ImageLoader.Builder(context = this)
                .allowHardware(false)
                .components {
                    add(DivkitAssetUriMapper())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        add(gifDecoderFactory())
                    } else {
                        add(GifFirstFrameDecoder.Factory())
                    }
                }
                .eventListener(imageLoadingTracker)
                .build()
        }

        divContext = DivContext(
            baseContext = this,
            configuration = DivComposeConfiguration(
                fontSourceProvider = ComposeFontSourceProvider(),
                playerFactory = ViewBasedDivVideoPlayerFactory(ExoDivPlayerFactory(this))
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
                view.removeAutofocusForOldApis()
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

/**
 * Decodes only the first frame of a GIF as a static bitmap.
 *
 * On API < 28 Coil's GIF decoder produces a MovieDrawable that animates via continuous Choreographer
 * frame callbacks on the main looper, which prevents Espresso from ever observing the looper as idle
 * (the snapshot then fails with AppNotIdleException). A static first frame is deterministic and lets
 * the looper settle.
 */
private class GifFirstFrameDecoder(
    private val source: ImageSource,
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val bytes = source.source().use { it.readByteArray() }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: error("Unable to decode the first GIF frame")
        return DecodeResult(image = bitmap.asImage(), isSampled = false)
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            if (!result.source.source().rangeEquals(0, GIF_HEADER)) return null
            return GifFirstFrameDecoder(result.source)
        }

        private companion object {
            val GIF_HEADER = "GIF8".encodeUtf8()
        }
    }
}
