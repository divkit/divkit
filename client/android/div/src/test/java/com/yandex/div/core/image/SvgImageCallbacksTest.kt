package com.yandex.div.core.image

import android.app.Activity
import android.os.Looper
import android.text.Spanned
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.descendants
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivKitConfiguration
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.spannable.ImageSpan
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import java.io.IOException
import java.time.Duration
import java.util.Collections
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class SvgImageCallbacksTest {

    private val hostLoader = mock<DivImageLoader>()
    private val mainThread: Thread get() = Looper.getMainLooper().thread

    @BeforeTest
    fun setUp() {
        DivKit.resetSingletonForTesting()
    }

    @AfterTest
    fun tearDown() {
        DivKit.resetSingletonForTesting()
    }

    @Test
    fun `svg success callback is delivered on main thread`() {
        assertEquals("onSuccess" to mainThread, load(svgClient()))
    }

    @Test
    fun `svg error callback is delivered on main thread`() {
        assertEquals("onError" to mainThread, load { throw IOException("offline") })
    }

    @Test
    fun `svg image in text is applied without crash`() {
        bind("""{"type": "text", "text": "text", "images": [{"url": "$SVG_URL", "start": 0}]}""") { view ->
            view.descendants.filterIsInstance<TextView>().any { textView ->
                val text = textView.text as? Spanned ?: return@any false
                text.getSpans(0, text.length, ImageSpan::class.java).any { it.image != null }
            }
        }
    }

    @Test
    fun `svg tab title delimiter is applied without crash`() {
        bind(
            """{
                "type": "tabs",
                "tab_title_delimiter": {"image_url": "$SVG_URL"},
                "items": [
                    {"title": "a", "div": {"type": "text", "text": "a"}},
                    {"title": "b", "div": {"type": "text", "text": "b"}}
                ]
            }"""
        ) { view ->
            view.descendants.filterIsInstance<ImageView>().any { it.drawable != null }
        }
    }

    private fun load(client: DivNetworkClient): Pair<String, Thread>? {
        val wrapper = DivImageLoaderWrapper(hostLoader, ApplicationProvider.getApplicationContext(), client)
        val result = AtomicReference<Pair<String, Thread>>()
        wrapper.loadImage(SVG_URL, object : DivImageDownloadCallback() {
            override fun onSuccess(cachedImage: DivCachedImage) = result.set("onSuccess" to Thread.currentThread())

            override fun onError(e: Throwable?) = result.set("onError" to Thread.currentThread())
        })
        pump { result.get() != null }
        return result.get()
    }

    private fun bind(div: String, isApplied: (Div2View) -> Boolean) {
        val gate = CompletableDeferred<Unit>()
        DivKit.configure(DivKitConfiguration.Builder().networkClient(svgClient(gate)).build())
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val view = Div2View(Div2Context(activity, DivConfiguration.Builder(hostLoader).build()))
        activity.setContentView(view)
        val card = JSONObject("""{"log_id": "svg", "states": [{"state_id": 0, "div": $div}]}""")
        view.setData(DivData(DivParsingEnvironment(ParsingErrorLogger.LOG), card), DivDataTag("svg"))
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(100))

        val uncaught = Collections.synchronizedList(mutableListOf<Throwable>())
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { _, e -> uncaught += e }
        try {
            gate.complete(Unit)
            pump { uncaught.isNotEmpty() || isApplied(view) }
            pump(SETTLE_MS) { uncaught.isNotEmpty() }
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(defaultHandler)
        }

        assertEquals(emptyList(), uncaught.toList())
        assertTrue(isApplied(view), "SVG image is not applied")
    }

    private fun pump(timeoutMs: Long = TIMEOUT_MS, isDone: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!isDone() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(FRAME_MS))
            Thread.sleep(FRAME_MS)
        }
    }

    private fun svgClient(gate: CompletableDeferred<Unit>? = null) = DivNetworkClient { request ->
        gate?.await()
        SvgResponse(request.url)
    }

    private class SvgResponse(override val url: String) : DivNetworkResponse {
        override val code = 200
        override val contentType = "image/svg+xml"
        override val body = DivNetworkResponseBody.fromBytes(SVG.toByteArray())
        override fun headers(name: String) = emptyList<String>()
        override fun close() = Unit
    }

    private companion object {
        const val SVG_URL = "https://example.com/image.svg"
        const val SVG = """<svg xmlns="http://www.w3.org/2000/svg" width="10" height="10"/>"""
        const val TIMEOUT_MS = 5_000L
        const val SETTLE_MS = 100L
        const val FRAME_MS = 16L
    }
}
