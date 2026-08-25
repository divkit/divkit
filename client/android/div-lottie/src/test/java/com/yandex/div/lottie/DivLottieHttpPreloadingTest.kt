package com.yandex.div.lottie

import android.net.Uri
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.network.OkHttpDivNetworkClient
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.Call
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivLottieHttpPreloadingTest {
    @Test
    fun `preload uses host network client`() {
        val url = Uri.parse("https://example.com/animation.json")
        val callFactory = mock<Call.Factory>()
        val call = mock<Call>()
        whenever(callFactory.newCall(any())).thenReturn(call)
        doAnswer { invocation ->
            val callback = invocation.getArgument<okhttp3.Callback>(0)
            callback.onResponse(
                call,
                Response.Builder()
                    .request(Request.Builder().url(url.toString()).build())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(MINIMAL_LOTTIE.toResponseBody())
                    .build(),
            )
        }.whenever(call).enqueue(any())
        val cache = object : DivLottieNetworkCache {
            override fun loadCached(url: String): String? = null
            override fun cacheComposition(url: String) = error("Legacy preload must not be used")
        }
        val repository = DivLottieCompositionRepository(
            cache,
            DivLottieLogger.STUB,
            OkHttpDivNetworkClient(callFactory),
            CoroutineScope(Dispatchers.Unconfined),
        )
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(url) { result = it as UriPreloadResult }
        repository.preloadLottieComposition(url) { result = it as UriPreloadResult }

        verify(callFactory, times(1)).newCall(any())
        Assert.assertNull(result?.error)
        Assert.assertEquals(url, result?.uri)
    }

    @Test
    fun `client supplied at handler creation is used`() {
        val url = Uri.parse("https://example.com/animation.json")
        var requestCount = 0
        val handler = DivLottieExtensionHandler(
            cache = DivLottieNetworkCache.STUB,
            asyncUpdatesEnabled = true,
            preloadScope = CoroutineScope(Dispatchers.Unconfined),
            networkClient = DivNetworkClient {
                requestCount++
                object : DivNetworkResponse {
                    override val url = url.toString()
                    override val code = 200
                    override val contentType = "application/json"
                    override val body = DivNetworkResponseBody.fromBytes(MINIMAL_LOTTIE.toByteArray())
                    override fun headers(name: String) = emptyList<String>()
                    override fun close() = Unit
                }
            },
        )
        var result: UriPreloadResult? = null

        handler.compositionRepository.preloadLottieComposition(url) { result = it as UriPreloadResult }

        Assert.assertNull(result?.error)
        Assert.assertEquals(1, requestCount)
    }

    @Test
    fun `synchronous network failure completes preload`() {
        val url = Uri.parse("https://example.com/animation.json")
        val cause = IllegalArgumentException("Malformed URL")
        val repository = DivLottieCompositionRepository(
            DivLottieNetworkCache.STUB,
            DivLottieLogger.STUB,
            DivNetworkClient { throw cause },
            CoroutineScope(Dispatchers.Unconfined),
        )
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(url) { result = it as UriPreloadResult }

        Assert.assertSame(cause, result?.error)
    }

    @Test
    fun `cancelled scope completes preload with cancellation error`() {
        val url = Uri.parse("https://example.com/animation.json")
        val networkScope = CoroutineScope(Job().apply { cancel() } + Dispatchers.Unconfined)
        val repository = DivLottieCompositionRepository(
            DivLottieNetworkCache.STUB,
            DivLottieLogger.STUB,
            DivNetworkClient { error("Network request must not be started") },
            networkScope,
        )
        var callbackCount = 0
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(url) {
            callbackCount++
            result = it as UriPreloadResult
        }

        Assert.assertEquals(1, callbackCount)
        Assert.assertTrue(result?.error is CancellationException)
    }

    @Test
    fun `specialized preload cache takes precedence over host network client`() {
        val url = Uri.parse("https://example.com/animation.json")
        val callFactory = mock<Call.Factory>()
        val cache = object : DivLottieNetworkCache {
            override fun loadCached(url: String): String? = null
            override fun cacheComposition(url: String) = Unit
            override fun cacheComposition(url: String, onComplete: (Throwable?) -> Unit): Boolean {
                onComplete(null)
                return true
            }
        }
        val repository = DivLottieCompositionRepository(
            cache,
            DivLottieLogger.STUB,
            OkHttpDivNetworkClient(callFactory),
            CoroutineScope(Dispatchers.Unconfined),
        )
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(url) { result = it as UriPreloadResult }

        verify(callFactory, never()).newCall(any())
        Assert.assertNull(result?.error)
    }

    @Test
    fun `generic content type with lottie extension is parsed as zip`() {
        val url = Uri.parse("https://example.com/ANIMATION.LoTtIe?download=1#preview")
        val repository = DivLottieCompositionRepository(
            DivLottieNetworkCache.STUB,
            DivLottieLogger.STUB,
            DivNetworkClient {
                object : DivNetworkResponse {
                    override val url = url.toString()
                    override val code = 200
                    override val contentType = "application/octet-stream"
                    override val body = DivNetworkResponseBody.fromBytes(zipLottie(MINIMAL_LOTTIE))
                    override fun headers(name: String) = emptyList<String>()
                    override fun close() = body.close()
                }
            },
            CoroutineScope(Dispatchers.Unconfined),
        )
        var result: UriPreloadResult? = null

        repository.preloadLottieComposition(url) { result = it as UriPreloadResult }

        Assert.assertNull(result?.error)
    }
}

private fun zipLottie(json: String): ByteArray {
    val output = ByteArrayOutputStream()
    ZipOutputStream(output).use { zip ->
        zip.putNextEntry(ZipEntry("data.json"))
        zip.write(json.toByteArray())
        zip.closeEntry()
    }
    return output.toByteArray()
}

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
