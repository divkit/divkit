package com.yandex.div.picasso

import android.os.Looper
import com.yandex.div.core.images.DivImageDownloadCallback
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Suppress("DEPRECATION")
class PicassoDivImageLoaderTest {

    @Test(timeout = 10_000)
    fun `loadImage uses configured OkHttpClient`() {
        val interceptedRequestUrl = AtomicReference<String>()
        val requestIntercepted = CountDownLatch(1)
        val httpClientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                interceptedRequestUrl.set(chain.request().url.toString())
                requestIntercepted.countDown()
                throw IOException("Stop after observing request")
            }
        val imageLoader = PicassoDivImageLoader(
            RuntimeEnvironment.application,
            httpClientBuilder,
        )

        val loadReference = imageLoader.loadImage(TEST_IMAGE_URL, DivImageDownloadCallback())
        shadowOf(Looper.getMainLooper()).idle()

        try {
            assertTrue(
                "The configured OkHttpClient did not receive the image request",
                requestIntercepted.await(5, TimeUnit.SECONDS),
            )
            assertEquals(TEST_IMAGE_URL, interceptedRequestUrl.get())
        } finally {
            loadReference.cancel()
        }
    }

    private companion object {
        const val TEST_IMAGE_URL = "http://127.0.0.1:1/image.png"
    }
}
