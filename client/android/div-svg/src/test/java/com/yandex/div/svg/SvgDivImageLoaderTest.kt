package com.yandex.div.svg

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.network.DivNetworkClient
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
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
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1\" height=\"1\"/>"
        val server = ServerSocket(0)
        val serverThread = thread { server.serveOnce(svg.toByteArray()) }
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
                .loadImage("http://127.0.0.1:${server.localPort}/image.svg", callback)

            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
            while (completed.count > 0 && System.nanoTime() < deadline) {
                shadowOf(Looper.getMainLooper()).idle()
                Thread.sleep(10)
            }
            assertTrue("SVG callback was not called", completed.count == 0L)
            assertNull(failure)
        } finally {
            server.close()
            serverThread.join(TimeUnit.SECONDS.toMillis(1))
        }
    }
}

/**
 * Serves [body] to the first accepted connection and closes it. A raw socket is used instead of
 * `com.sun.net.httpserver`, which is not part of the Java 8 API this module compiles against.
 */
private fun ServerSocket.serveOnce(body: ByteArray) {
    try {
        accept().use { socket ->
            val request = socket.getInputStream().bufferedReader()
            while (true) {
                val line = request.readLine() ?: break
                if (line.isEmpty()) break
            }
            val headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: image/svg+xml\r\n" +
                "Content-Length: ${body.size}\r\n" +
                "Connection: close\r\n\r\n"
            socket.getOutputStream().apply {
                write(headers.toByteArray())
                write(body)
                flush()
            }
        }
    } catch (e: IOException) {
        // The socket is closed by the test once it no longer needs the server.
    }
}
