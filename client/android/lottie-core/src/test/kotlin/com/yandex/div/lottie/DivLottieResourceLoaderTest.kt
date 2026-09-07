package com.yandex.div.lottie

import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivLottieResourceLoaderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `loader returns ready composition`() = runTest {
        val composition = composition()
        val loader = ComposeRecordingLoader { LottieResult(composition) }

        val result = loader.load(COMPOSE_URL)

        Assert.assertSame(composition, result.value)
    }

    @Test
    fun `composite delegates to first loader that claims URL`() = runTest {
        val expected = composition()
        val skipped = ComposeRecordingLoader(canLoadPredicate = { false }) { error("Must not load") }
        val selected = ComposeRecordingLoader(canLoadPredicate = { it == COMPOSE_URL }) { LottieResult(expected) }
        val trailing = ComposeRecordingLoader { error("Must not load") }
        val composite = CompositeDivLottieResourceLoader(skipped, selected, trailing)

        val result = composite.load(COMPOSE_URL)

        Assert.assertSame(expected, result.value)
        Assert.assertEquals(listOf(COMPOSE_URL), selected.loadedUrls)
        Assert.assertTrue(trailing.loadedUrls.isEmpty())
    }

    @Test
    fun `composite resolves the same URL once`() = runTest {
        var resolutionCount = 0
        val loader = ComposeRecordingLoader(
            canLoadPredicate = {
                resolutionCount++
                true
            },
        ) { LottieResult(composition()) }
        val composite = CompositeDivLottieResourceLoader(loader)

        Assert.assertTrue(composite.canLoad(COMPOSE_URL))
        Assert.assertTrue(composite.canLoad(COMPOSE_URL))
        Assert.assertNotNull(composite.load(COMPOSE_URL).value)

        Assert.assertEquals(1, resolutionCount)
    }

    @Test
    fun `composite reports URL that no loader claims`() = runTest {
        val composite = CompositeDivLottieResourceLoader(
            ComposeRecordingLoader(canLoadPredicate = { false }) { error("Must not load") },
        )

        val result = composite.load(COMPOSE_URL)

        Assert.assertTrue(result.exception is IllegalArgumentException)
    }

    @Test
    fun `asset loader claims mapped URL`() {
        val loader = DivLottieAssetResourceLoader(context) { "minimal_lottie.json" }

        Assert.assertTrue(loader.canLoad("asset://animation"))
    }

    @Test
    fun `asset loader skips unmapped URL`() {
        val loader = DivLottieAssetResourceLoader(context) { null }

        Assert.assertFalse(loader.canLoad("asset://missing"))
    }

    @Test
    fun `asset loader skips other schemes without invoking mapper`() {
        val loader = DivLottieAssetResourceLoader(context) { error("Must not resolve") }

        Assert.assertFalse(loader.canLoad("res://animation"))
    }

    @Test
    fun `composite falls through unmapped asset URL`() = runTest {
        val expected = composition()
        val composite = CompositeDivLottieResourceLoader(
            DivLottieAssetResourceLoader(context) { null },
            ComposeRecordingLoader { LottieResult(expected) },
        )

        Assert.assertSame(expected, composite.load("asset://animation").value)
    }

    @Test
    fun `asset loader returns terminal error when mapping is missing`() = runTest {
        val loader = DivLottieAssetResourceLoader(context) { null }

        val result = loader.load("asset://missing")

        Assert.assertTrue(result.exception is IllegalArgumentException)
    }

    @Test
    fun `asset loader loads mapped composition`() = runTest {
        val loader = DivLottieAssetResourceLoader(context) { "minimal_lottie.json" }

        val result = loader.load("asset://animation")

        Assert.assertNotNull(result.value)
    }

    @Test
    fun `raw resource loader claims mapped URL`() {
        val loader = DivLottieRawResResourceLoader(context) { 42 }

        Assert.assertTrue(loader.canLoad("res://animation"))
    }

    @Test
    fun `raw resource loader skips unmapped URL`() {
        val loader = DivLottieRawResResourceLoader(context) { null }

        Assert.assertFalse(loader.canLoad("res://missing"))
    }

    @Test
    fun `raw resource loader skips other schemes without invoking mapper`() {
        val loader = DivLottieRawResResourceLoader(context) { error("Must not resolve") }

        Assert.assertFalse(loader.canLoad("asset://animation"))
    }

    @Test
    fun `composite falls through unmapped raw resource URL`() = runTest {
        val expected = composition()
        val composite = CompositeDivLottieResourceLoader(
            DivLottieRawResResourceLoader(context) { null },
            ComposeRecordingLoader { LottieResult(expected) },
        )

        Assert.assertSame(expected, composite.load("res://animation").value)
    }

    @Test
    fun `raw resource loader returns terminal error when mapping is missing`() = runTest {
        val loader = DivLottieRawResResourceLoader(context) { null }

        val result = loader.load("res://missing")

        Assert.assertTrue(result.exception is IllegalArgumentException)
    }

    @Test
    fun `raw resource loader loads mapped composition`() = runTest {
        val resourceId = context.resources.getIdentifier(
            "minimal_lottie",
            "raw",
            context.packageName,
        )
        val loader = DivLottieRawResResourceLoader(context) { resourceId }

        val result = loader.load("res://animation")

        Assert.assertNotEquals(0, resourceId)
        Assert.assertNotNull(result.value)
    }

    @Test
    fun `raw resource loader uses configuration from supplied context`() = runTest {
        val nightConfiguration = Configuration(context.resources.configuration).apply {
            uiMode = uiMode and Configuration.UI_MODE_NIGHT_MASK.inv() or
                Configuration.UI_MODE_NIGHT_YES
        }
        val nightContext = context.createConfigurationContext(nightConfiguration)
        val resourceId = nightContext.resources.getIdentifier(
            "minimal_lottie",
            "raw",
            nightContext.packageName,
        )
        val loader = DivLottieRawResResourceLoader(nightContext) { resourceId }

        val composition = loader.load("res://night-animation").value

        Assert.assertNotEquals(0, resourceId)
        Assert.assertEquals(60f, requireNotNull(composition).frameRate, 0f)
    }

    @Test
    fun `raw resource loaders keep different resources at the same URL separate`() = runTest {
        val firstId = context.resources.getIdentifier("minimal_lottie", "raw", context.packageName)
        val secondId = context.resources.getIdentifier("fast_lottie", "raw", context.packageName)
        val firstLoader = DivLottieRawResResourceLoader(context) { firstId }
        val secondLoader = DivLottieRawResResourceLoader(context) { secondId }
        Assert.assertNotNull(firstLoader.load("res://cache-isolation").value)

        val composition = secondLoader.load("res://cache-isolation").value

        Assert.assertEquals(60f, requireNotNull(composition).frameRate, 0f)
    }
}

private class ComposeRecordingLoader(
    private val canLoadPredicate: (String) -> Boolean = { true },
    private val result: suspend (String) -> LottieResult<LottieComposition>,
) : DivLottieResourceLoader {
    val loadedUrls = mutableListOf<String>()

    override fun canLoad(url: String): Boolean = canLoadPredicate(url)

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        loadedUrls += url
        return result(url)
    }
}

private fun composition(): LottieComposition {
    return requireNotNull(LottieCompositionFactory.fromJsonStringSync(MINIMAL_LOTTIE, null).value)
}

private const val COMPOSE_URL = "divkit-resource://animations/compose"
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
