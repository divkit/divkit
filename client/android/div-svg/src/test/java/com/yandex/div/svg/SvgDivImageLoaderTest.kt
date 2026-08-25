package com.yandex.div.svg

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.sun.net.httpserver.HttpServer
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.network.DivNetworkClient
import java.net.InetSocketAddress
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
public class SvgDivImageLoaderTest {

    @Test
    public fun `cancelling provided scope cancels network request`() {
        val started = CountDownLatch(1)
        val cancelled = CountDownLatch(1)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val client = DivNetworkClient {
            suspendCancellableCoroutine { continuation ->
                started.countDown()
                continuation.invokeOnCancellation { cancelled.countDown() }
            }
        }
        val loader = SvgDivImageLoader(
            ApplicationProvider.getApplicationContext(),
            client,
            scope,
        )

        loader.loadImage("https://example.com/image.svg", object : DivImageDownloadCallback() {})
        assertTrue(started.await(5, TimeUnit.SECONDS))

        scope.cancel()

        assertTrue(cancelled.await(5, TimeUnit.SECONDS))
    }

    @Test
    public fun `loads HTTP SVG without configured network client`() {
        val server = HttpServer.create(InetSocketAddress(0), 0)
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1\" height=\"1\"/>"
        server.createContext("/image.svg") { exchange ->
            val body = svg.toByteArray()
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()
        try {
            val completed = CountDownLatch(1)
            var failure: Throwable? = null
            val callback = object : DivImageDownloadCallback() {
                override fun onSuccess(cachedImage: DivCachedImage) {
                    completed.countDown()
                }

                override fun onError(error: Throwable?) {
                    failure = error
                    completed.countDown()
                }
            }

            SvgDivImageLoader(ApplicationProvider.getApplicationContext())
                .loadImage("http://127.0.0.1:${server.address.port}/image.svg", callback)

            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
            while (completed.count > 0 && System.nanoTime() < deadline) {
                shadowOf(Looper.getMainLooper()).idle()
                Thread.sleep(10)
            }
            assertTrue("SVG callback was not called", completed.count == 0L)
            assertNull(failure)
        } finally {
            server.stop(0)
        }
    }
}
