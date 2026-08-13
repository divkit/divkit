package com.yandex.div.lottie

import android.content.Context
import android.os.Looper
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.model.LottieCompositionCache
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(RobolectricTestRunner::class)
class DivLottieInlinePreloadTest {

    private val repo = DivLottieCompositionRepository(DivLottieNetworkCache.STUB, DivLottieLogger.STUB)
    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Before
    fun clearLottieCache() {
        LottieCompositionCache.getInstance().clear()
    }

    @Test
    fun `preloadInlineComposition puts composition into lottie cache under bind cache key`() {
        val data = LottieData.Json(MINIMAL_LOTTIE_JSON)
        var result: UriPreloadResult? = null

        repo.preloadInlineComposition(data) { result = it as UriPreloadResult }

        Assert.assertNotNull(result)
        Assert.assertNull(result?.error)
        Assert.assertNotNull(LottieCompositionCache.getInstance().get(data.cacheKey))
    }

    @Test
    fun `bind receives preloaded composition instance without reparsing`() {
        val data = LottieData.Json(MINIMAL_LOTTIE_JSON)
        repo.preloadInlineComposition(data) { }
        val preloaded = LottieCompositionCache.getInstance().get(data.cacheKey)

        val bindResult = repo.receiveLottieComposition(data, context)

        Assert.assertSame(preloaded, bindResult.value)
    }

    @Test
    fun `preloadInlineComposition reports parsing error`() {
        val data = LottieData.Json("not a json at all")
        var result: UriPreloadResult? = null

        repo.preloadInlineComposition(data) { result = it as UriPreloadResult }

        Assert.assertNotNull(result?.error)
    }

    @Test
    fun `receiveLottieComposition decodes embedded base64 images`() {
        val data = LottieData.Json(LOTTIE_JSON_WITH_IMAGE)

        val result = repo.receiveLottieComposition(data, context)

        val asset = result.value!!.images.values.single()
        val bitmap = asset.bitmap
        Assert.assertNotNull(bitmap)
        Assert.assertEquals(asset.width, bitmap!!.width)
        Assert.assertEquals(asset.height, bitmap.height)
    }

    @Test
    fun `file based image asset is left for lazy decoding`() {
        val data = LottieData.Json(LOTTIE_JSON_WITH_FILE_IMAGE)

        val result = repo.receiveLottieComposition(data, context)

        val asset = result.value!!.images.values.single()
        Assert.assertNull(asset.bitmap)
    }

    @Test
    fun `broken base64 image does not fail composition receive`() {
        val data = LottieData.Json(LOTTIE_JSON_WITH_BROKEN_IMAGE)

        val result = repo.receiveLottieComposition(data, context)

        Assert.assertNotNull(result.value)
    }

    @Test
    fun `image asset without dimensions is left for lazy decoding`() {
        val data = LottieData.Json(LOTTIE_JSON_WITH_ZERO_DIM_IMAGE)

        val result = repo.receiveLottieComposition(data, context)

        val asset = result.value!!.images.values.single()
        Assert.assertNull(asset.bitmap)
    }

    @Test
    fun `concurrent preloads of the same inline json all complete`() {
        val data = LottieData.Json(MINIMAL_LOTTIE_JSON)
        val threads = 8
        val completions = CountDownLatch(threads)
        val errors = AtomicBoolean()
        val executor = Executors.newFixedThreadPool(threads)

        repeat(threads) {
            executor.execute {
                repo.preloadInlineComposition(data) { result ->
                    if ((result as UriPreloadResult).error != null) errors.set(true)
                    completions.countDown()
                }
            }
        }

        Assert.assertTrue(completions.await(5, TimeUnit.SECONDS))
        executor.shutdown()
        Assert.assertFalse(errors.get())
        val cached = LottieCompositionCache.getInstance().get(data.cacheKey)
        Assert.assertNotNull(cached)
        Assert.assertSame(cached, repo.receiveLottieComposition(data, context).value)
    }

    @Test
    fun `concurrent cold cache receives are deduplicated to a single composition instance`() {
        val data = LottieData.Json(MINIMAL_LOTTIE_JSON)
        val threads = 8
        val barrier = CyclicBarrier(threads)
        val results = Collections.synchronizedList(mutableListOf<LottieComposition>())
        val done = CountDownLatch(threads)
        val executor = Executors.newFixedThreadPool(threads)

        repeat(threads) {
            executor.execute {
                barrier.await()
                repo.receiveLottieComposition(data, context).value?.let { results.add(it) }
                done.countDown()
            }
        }

        Assert.assertTrue(done.await(5, TimeUnit.SECONDS))
        executor.shutdown()
        Assert.assertEquals(threads, results.size)
        results.forEach { Assert.assertSame(results[0], it) }
    }

    @Test
    fun `preprocess with inline json warms up composition cache`() {
        val cardJson = JSONObject(DIV_JSON_WITH_INLINE_LOTTIE)

        val hadErrors = runPreload(DivLottieExtensionHandler(), cardJson)

        Assert.assertFalse(hadErrors)
        Assert.assertNotNull(LottieCompositionCache.getInstance().get(expectedInlineData(cardJson).cacheKey))
    }

    @Test
    fun `preload is skipped when async updates disabled`() {
        val cardJson = JSONObject(DIV_JSON_WITH_INLINE_LOTTIE)

        val hadErrors = runPreload(DivLottieExtensionHandler(asyncUpdatesEnabled = false), cardJson)

        Assert.assertFalse(hadErrors)
        Assert.assertNull(LottieCompositionCache.getInstance().get(expectedInlineData(cardJson).cacheKey))
    }

    @Test
    fun `cancelled preload scope completes registered preload with error`() {
        val cardJson = JSONObject(DIV_JSON_WITH_INLINE_LOTTIE)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.cancel()
        val handler = DivLottieExtensionHandler(asyncUpdatesEnabled = true, preloadScope = scope)

        val hadErrors = runPreload(handler, cardJson)

        Assert.assertTrue(hadErrors)
        Assert.assertNull(LottieCompositionCache.getInstance().get(expectedInlineData(cardJson).cacheKey))
    }

    private fun runPreload(handler: DivLottieExtensionHandler, cardJson: JSONObject): Boolean {
        val divData = DivData(DivParsingEnvironment(ParsingErrorLogger.ASSERT), cardJson)
        val configuration = DivConfiguration.Builder(StubImageLoader())
            .extension(handler)
            .build()
        val preloader = DivPreloader(configuration)
        val finished = AtomicBoolean()
        val hadErrors = AtomicBoolean()

        divData.states.forEach { state ->
            preloader.preload(state.div, ExpressionResolver.EMPTY) { errors ->
                hadErrors.set(errors)
                finished.set(true)
            }
        }
        awaitMainLooper(finished)

        Assert.assertTrue(finished.get())
        return hadErrors.get()
    }

    private fun expectedInlineData(cardJson: JSONObject): LottieData.Json {
        val params = cardJson.getJSONArray("states").getJSONObject(0).getJSONObject("div")
            .getJSONArray("extensions").getJSONObject(0).getJSONObject("params")
        return LottieData.Json(params.getJSONObject("lottie_json").toString())
    }

    private fun awaitMainLooper(condition: AtomicBoolean) {
        val deadline = System.currentTimeMillis() + 5000
        while (!condition.get() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(10)
        }
        shadowOf(Looper.getMainLooper()).idle()
    }
}

private val StubLoadReference = LoadReference {}

private class StubImageLoader : DivImageLoader {

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference = StubLoadReference

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference = StubLoadReference

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference =
        StubLoadReference
}

private const val PIXEL_PNG_BASE64 =
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4z8AAAAMBAQDJ/pLvAAAAAElFTkSuQmCC"

private const val MINIMAL_LOTTIE_JSON = """
{"v":"5.5.2","fr":30,"ip":0,"op":30,"w":10,"h":10,"nm":"comp","ddd":0,"assets":[],
"layers":[{"ddd":0,"ind":1,"ty":1,"nm":"solid","sr":1,"ks":{},"ao":0,"sw":10,"sh":10,"sc":"#ff0000","ip":0,"op":30,"st":0,"bm":0}]}
"""

private const val LOTTIE_JSON_WITH_IMAGE = """
{"v":"5.5.2","fr":30,"ip":0,"op":30,"w":1,"h":1,"nm":"comp","ddd":0,
"assets":[{"id":"image_0","w":1,"h":1,"u":"","p":"data:image/png;base64,$PIXEL_PNG_BASE64","e":1}],
"layers":[{"ddd":0,"ind":1,"ty":2,"nm":"image","refId":"image_0","sr":1,"ks":{},"ao":0,"ip":0,"op":30,"st":0,"bm":0}]}
"""

private const val LOTTIE_JSON_WITH_BROKEN_IMAGE = """
{"v":"5.5.2","fr":30,"ip":0,"op":30,"w":1,"h":1,"nm":"comp","ddd":0,
"assets":[{"id":"image_0","w":1,"h":1,"u":"","p":"data:image/png;base64,!!!not-a-base64!!!","e":1}],
"layers":[{"ddd":0,"ind":1,"ty":2,"nm":"image","refId":"image_0","sr":1,"ks":{},"ao":0,"ip":0,"op":30,"st":0,"bm":0}]}
"""

private const val LOTTIE_JSON_WITH_FILE_IMAGE = """
{"v":"5.5.2","fr":30,"ip":0,"op":30,"w":1,"h":1,"nm":"comp","ddd":0,
"assets":[{"id":"image_0","w":1,"h":1,"u":"images/","p":"img_0.png"}],
"layers":[{"ddd":0,"ind":1,"ty":2,"nm":"image","refId":"image_0","sr":1,"ks":{},"ao":0,"ip":0,"op":30,"st":0,"bm":0}]}
"""

private const val LOTTIE_JSON_WITH_ZERO_DIM_IMAGE = """
{"v":"5.5.2","fr":30,"ip":0,"op":30,"w":1,"h":1,"nm":"comp","ddd":0,
"assets":[{"id":"image_0","u":"","p":"data:image/png;base64,$PIXEL_PNG_BASE64","e":1}],
"layers":[{"ddd":0,"ind":1,"ty":2,"nm":"image","refId":"image_0","sr":1,"ks":{},"ao":0,"ip":0,"op":30,"st":0,"bm":0}]}
"""

private const val DIV_JSON_WITH_INLINE_LOTTIE = """
{
  "log_id": "div_card_with_inline_lottie",
  "states": [
    {
      "state_id": 0,
      "div": {
        "type": "gif",
        "gif_url": "https://none",
        "extensions": [
          {
            "id": "lottie",
            "params": {
              "lottie_json": {"v":"5.5.2","fr":30,"ip":0,"op":30,"w":10,"h":10,"nm":"comp","ddd":0,"assets":[],
                "layers":[{"ddd":0,"ind":1,"ty":1,"nm":"solid","sr":1,"ks":{},"ao":0,"sw":10,"sh":10,"sc":"#ff0000","ip":0,"op":30,"st":0,"bm":0}]}
            }
          }
        ]
      }
    }
  ]
}
"""
