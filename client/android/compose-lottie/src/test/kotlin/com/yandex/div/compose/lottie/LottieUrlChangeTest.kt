package com.yandex.div.compose.lottie

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.DivView
import com.yandex.div.lottie.DivLottieResourceLoader
import com.yandex.div.test.data.data
import com.yandex.div2.Div
import com.yandex.div2.DivExtension
import com.yandex.div2.DivImage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LottieUrlChangeTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `pending URL does not inherit previous load error`() {
        // Arrange: the first URL fails; the second load remains pending.
        val errors = mutableListOf<String>()
        val loader = PendingSecondLoader()
        val currentData = mutableStateOf(lottieData(FIRST_URL))
        val configuration = configuration(loader, errors)
        rule.setContent {
            val baseContext = LocalContext.current
            val context = remember(baseContext) { DivContext(baseContext, configuration) }
            CompositionLocalProvider(LocalContext provides context) { DivView(currentData.value) }
        }
        rule.waitUntil { errors.isNotEmpty() }
        Assert.assertEquals(listOf("Failed to load Lottie composition from $FIRST_URL: first failed"), errors)

        // Act: replace the failed URL with a pending one at the same UI position.
        rule.runOnIdle { currentData.value = lottieData(SECOND_URL) }
        rule.waitForIdle()
        rule.waitUntil(timeoutMillis = 5_000) { loader.secondStarted.isCompleted }
        rule.waitForIdle()

        // Assert: only the first URL's own error was reported.
        Assert.assertEquals(listOf("Failed to load Lottie composition from $FIRST_URL: first failed"), errors)
    }

    @Test
    fun `failed URL is reported once across recomposition`() {
        val errors = mutableListOf<String>()
        val loader = RecordingLoader { LottieResult(IllegalStateException("load failed")) }
        val currentData = mutableStateOf(lottieData(FIRST_URL, isPlaying = true))
        val configuration = configuration(loader, errors)
        rule.setContent {
            val baseContext = LocalContext.current
            val context = remember(baseContext) { DivContext(baseContext, configuration) }
            CompositionLocalProvider(LocalContext provides context) { DivView(currentData.value) }
        }
        rule.waitUntil { errors.size == 1 }

        rule.runOnIdle { currentData.value = lottieData(FIRST_URL, isPlaying = false) }
        rule.waitForIdle()

        Assert.assertEquals(
            listOf("Failed to load Lottie composition from $FIRST_URL: load failed"),
            errors,
        )
    }

    @Test
    fun `preload warms handler cache for bind`() = runTest {
        val loader = RecordingLoader { LottieResult(composition()) }
        val configuration = configuration(loader, mutableListOf())
        val baseContext = ApplicationProvider.getApplicationContext<Context>()
        val context = DivContext(baseContext, configuration)
        val data = lottieData(FIRST_URL)

        context.preload(data)
        Assert.assertEquals(listOf(FIRST_URL), loader.loadedUrls)

        rule.setContent {
            CompositionLocalProvider(LocalContext provides context) { DivView(data) }
        }
        rule.waitForIdle()

        Assert.assertEquals(listOf(FIRST_URL), loader.loadedUrls)
    }
}

private fun configuration(loader: DivLottieResourceLoader, errors: MutableList<String>) = DivConfiguration(
    extensionHandlers = mapOf("lottie" to LottieExtensionHandler(loader)),
    reporter = object : DivReporter() {
        override fun reportError(message: String) {
            errors += message
        }
    },
)

private fun lottieData(url: String, isPlaying: Boolean? = null) = data(
    Div.Image(DivImage(
        extensions = listOf(DivExtension(
            id = "lottie",
            params = JSONObject().put("lottie_url", url).apply {
                isPlaying?.let { put("is_playing", it) }
            },
        )),
    )),
)

private class RecordingLoader(
    private val result: suspend (String) -> LottieResult<LottieComposition>,
) : DivLottieResourceLoader {
    val loadedUrls = mutableListOf<String>()

    override fun canLoad(url: String): Boolean = true

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        loadedUrls += url
        return result(url)
    }
}

private class PendingSecondLoader : DivLottieResourceLoader {
    val secondStarted = CompletableDeferred<Unit>()
    private val pending = CompletableDeferred<LottieResult<LottieComposition>>()

    override fun canLoad(url: String): Boolean = true

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        if (url == FIRST_URL) return LottieResult(IllegalStateException("first failed"))
        secondStarted.complete(Unit)
        return pending.await()
    }
}

private const val FIRST_URL = "custom://first"
private const val SECOND_URL = "custom://second"
private const val MINIMAL_LOTTIE = """
{
  "v": "5.5.7",
  "fr": 30,
  "ip": 0,
  "op": 1,
  "w": 1,
  "h": 1,
  "layers": []
}
"""

private fun composition(): LottieComposition {
    return requireNotNull(LottieCompositionFactory.fromJsonStringSync(MINIMAL_LOTTIE, null).value)
}
