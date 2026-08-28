package com.yandex.div.core.image

import androidx.test.core.app.ApplicationProvider
import android.os.Looper
import org.robolectric.Shadows.shadowOf
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.network.DivNetworkClient
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.timeout
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivImageLoaderWrapperTest {

    @Test
    fun `SVG fallback uses host network client`() {
        val networkClient = mock<DivNetworkClient>()
        runBlocking { whenever(networkClient.execute(any())).thenThrow(IllegalStateException("stop")) }
        val providedLoader = mock<DivImageLoader> {
            on { hasSvgSupport() } doReturn false
        }
        val wrapper = DivImageLoaderWrapper(
            providedLoader,
            ApplicationProvider.getApplicationContext(),
            networkClient,
        )

        wrapper.loadImage("https://example.com/image.svg", mock<DivImageDownloadCallback>())
        shadowOf(Looper.getMainLooper()).idle()

        runBlocking { verify(networkClient, timeout(5_000)).execute(any()) }
    }
}
