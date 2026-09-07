package com.yandex.div.lottie

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import com.airbnb.lottie.model.LottieCompositionCache
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.internal.extensions.lottie.LottieData
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
class DivLottieResourceLoaderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun clearLottieCache() {
        LottieCompositionCache.getInstance().clear()
    }

    @Test
    fun `claimed composition is loaded through existing handler`() = runTest {
        val composition = composition()
        val loader = RecordingLoader { LottieResult(composition) }
        val handler = DivLottieExtensionHandler(
            resourceLoader = loader,
            asyncUpdatesEnabled = true,
            cache = FailingLegacyCache,
            preloadScope = CoroutineScope(Dispatchers.Unconfined),
            networkClient = FailingNetworkClient,
        )

        val result = handler.compositionRepository.receiveLottieCompositionAsync(urlData(), context)

        Assert.assertSame(composition, result.value)
        Assert.assertEquals(listOf(CUSTOM_URL), loader.loadedUrls)
    }

    @Test
    fun `preload warms composition cache for bind`() = runTest {
        val composition = composition()
        val loader = RecordingLoader { LottieResult(composition) }
        val repository = repository(loader)
        var preloadResult: UriPreloadResult? = null

        repository.preloadLottieComposition(Uri.parse(CUSTOM_URL)) {
            preloadResult = it as UriPreloadResult
        }
        val bindResult = repository.receiveLottieCompositionAsync(urlData(), context)

        Assert.assertNull(preloadResult?.error)
        Assert.assertSame(composition, bindResult.value)
        Assert.assertEquals(1, loader.loadedUrls.size)
    }

    @Test
    fun `composition cache is isolated per handler`() = runTest {
        val first = repository(RecordingLoader { LottieResult(composition(1)) })
        val second = repository(RecordingLoader { LottieResult(composition(2)) })

        val firstResult = first.receiveLottieCompositionAsync(urlData(), context)
        val secondResult = second.receiveLottieCompositionAsync(urlData(), context)

        Assert.assertEquals(1, firstResult.value?.bounds?.width())
        Assert.assertEquals(2, secondResult.value?.bounds?.width())
    }

    @Test
    fun `claimed failure is terminal without legacy fallback`() = runTest {
        val cause = IOException("Host resource failed")
        val loader = RecordingLoader { LottieResult(cause) }
        val repository = repository(loader)

        val result = repository.receiveLottieCompositionAsync(urlData(), context)

        Assert.assertSame(cause, result.exception)
        Assert.assertEquals(1, loader.loadedUrls.size)
    }

    @Test
    fun `unclaimed HTTP URL keeps legacy cache fallback`() = runTest {
        val loader = RecordingLoader(canLoad = { false }) {
            error("Unclaimed URL must not be loaded")
        }
        val cache = RecordingLegacyCache(MINIMAL_LOTTIE)
        val repository = repository(loader, networkCache = cache)

        val result = repository.receiveLottieCompositionAsync(urlData(HTTP_URL), context)

        Assert.assertNotNull(result.value)
        Assert.assertEquals(1, cache.loadCount)
        Assert.assertTrue(loader.loadedUrls.isEmpty())
    }

    @Test
    fun `unclaimed HTTP preload still warms disk cache after memory hit`() = runTest {
        val loader = RecordingLoader(canLoad = { false }) {
            error("Unclaimed URL must not be loaded")
        }
        val cache = RecordingLegacyCache(MINIMAL_LOTTIE)
        val repository = repository(loader, networkCache = cache)
        Assert.assertNotNull(repository.receiveLottieCompositionAsync(urlData(HTTP_URL), context).value)
        var preloadResult: UriPreloadResult? = null

        repository.preloadLottieComposition(Uri.parse(HTTP_URL)) {
            preloadResult = it as UriPreloadResult
        }

        Assert.assertNull(preloadResult?.error)
        Assert.assertEquals(1, cache.preloadCount)
    }

    @Test
    fun `thrown cancellation is propagated`() = runTest {
        val cancellation = CancellationException("cancelled by caller")
        val repository = repository(RecordingLoader { throw cancellation })

        val thrown = assertFailsWith<CancellationException> {
            repository.receiveLottieCompositionAsync(urlData(), context)
        }

        Assert.assertSame(cancellation, thrown)
    }

    @Test
    fun `cancellation returned as failure is propagated`() = runTest {
        val cancellation = CancellationException("incorrectly wrapped cancellation")
        val repository = repository(RecordingLoader { LottieResult(cancellation) })

        val thrown = assertFailsWith<CancellationException> {
            repository.receiveLottieCompositionAsync(urlData(), context)
        }

        Assert.assertSame(cancellation, thrown)
    }

    @Test
    fun `claimed preload failure is terminal without legacy fallback`() {
        val cause = IOException("Host resource failed")
        val loader = RecordingLoader { LottieResult(cause) }
        val repository = repository(loader)
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(Uri.parse(CUSTOM_URL)) {
            result = it as UriPreloadResult
        }

        Assert.assertSame(cause, result?.error)
        Assert.assertEquals(1, loader.loadedUrls.size)
    }

    @Test
    fun `preload cancellation completes once with cancellation error`() {
        val cancellation = CancellationException("incorrectly wrapped cancellation")
        val repository = repository(RecordingLoader { LottieResult(cancellation) })
        var completionCount = 0
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(Uri.parse(CUSTOM_URL)) {
            completionCount++
            result = it as UriPreloadResult
        }

        Assert.assertSame(cancellation, result?.error)
        Assert.assertEquals(1, completionCount)
    }

    private fun repository(
        loader: DivLottieResourceLoader,
        networkCache: DivLottieNetworkCache = FailingLegacyCache,
    ): DivLottieCompositionRepository {
        return DivLottieExtensionHandler(
            resourceLoader = loader,
            asyncUpdatesEnabled = true,
            cache = networkCache,
            preloadScope = CoroutineScope(Dispatchers.Unconfined),
            networkClient = FailingNetworkClient,
        ).compositionRepository
    }
}

private class RecordingLoader(
    private val canLoad: (String) -> Boolean = { true },
    private val result: suspend (String) -> LottieResult<LottieComposition>,
) : DivLottieResourceLoader {
    val loadedUrls = mutableListOf<String>()

    override fun canLoad(url: String): Boolean = canLoad.invoke(url)

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        loadedUrls += url
        return result(url)
    }
}

private class RecordingLegacyCache(
    private val cachedJson: String,
) : DivLottieNetworkCache {
    var loadCount = 0
    var preloadCount = 0

    override fun loadCached(url: String): String {
        loadCount++
        return cachedJson
    }

    override fun cacheComposition(url: String) = Unit

    override fun cacheComposition(url: String, onComplete: (Throwable?) -> Unit): Boolean {
        preloadCount++
        onComplete(null)
        return true
    }
}

private object FailingLegacyCache : DivLottieNetworkCache {
    override fun loadCached(url: String): String? = error("Legacy cache must not be used")
    override fun cacheComposition(url: String) = error("Legacy cache must not be used")
    override fun cacheComposition(url: String, onComplete: (Throwable?) -> Unit): Boolean {
        error("Legacy cache must not be used")
    }
}

private val FailingNetworkClient = DivNetworkClient {
    error("Legacy network must not be used")
}

private fun composition(width: Int = 1): LottieComposition {
    return requireNotNull(LottieCompositionFactory.fromJsonStringSync(lottieJson(width), null).value)
}

private fun urlData(url: String = CUSTOM_URL) = LottieData.Url(url)

private fun lottieJson(width: Int): String = MINIMAL_LOTTIE.replace("\"w\": 1", "\"w\": $width")

private const val CUSTOM_URL = "divkit-resource://animations/sample"
private const val HTTP_URL = "https://example.com/animation.json"
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
