package com.yandex.div.core.image

import android.graphics.drawable.Drawable
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.DivImagePriority
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.network.DivNetworkClient
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Shadows.shadowOf
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivImageLoaderWrapperTest {
    private val loadReference = LoadReference {}
    private val providedLoader =
        mock<DivImageLoader> {
            on { hasSvgSupport() } doReturn true
            on { needLimitBitmapSize() } doReturn false
            on { loadImage(any(), any<DivImageDownloadCallback>()) } doReturn loadReference
            on { loadImage(any(), any(), any()) } doReturn loadReference
            on { loadImageBytes(any(), any(), any()) } doReturn loadReference
            on { loadAnimatedImage(any(), any(), any()) } doReturn loadReference
        }
    private val wrapper = createWrapper(providedLoader)

    @Test
    fun `loadImage forwards preload priority`() {
        val callback = mock<DivImageDownloadCallback>()

        wrapper.loadImage(IMAGE_URL, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)

        verify(providedLoader).loadImage(IMAGE_URL, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)
    }

    @Test
    fun `loadImageBytes forwards preload priority`() {
        val callback = mock<DivImageDownloadCallback>()

        wrapper.loadImageBytes(IMAGE_URL, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)

        verify(providedLoader).loadImageBytes(IMAGE_URL, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)
    }

    @Test
    fun `loadAnimatedImage forwards preload priority`() {
        val callback = mock<DivImageDownloadCallback>()

        wrapper.loadAnimatedImage(IMAGE_URL, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)

        verify(providedLoader).loadAnimatedImage(IMAGE_URL, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)
    }

    @Test
    fun `loadImage without priority uses bind-time overload`() {
        val callback = mock<DivImageDownloadCallback>()

        wrapper.loadImage(IMAGE_URL, callback)

        verify(providedLoader).loadImage(IMAGE_URL, callback)
        verify(providedLoader, never()).loadImage(any(), any(), any())
    }

    @Test
    fun `terminal callbacks are not filtered`() {
        val expectedError = IllegalStateException()
        val expectedImage = DivCachedImage.Drawable(mock<Drawable>(), BitmapSource.MEMORY)
        val deliveries = mutableListOf<Any>()
        val callback =
            object : DivImageDownloadCallback() {
                override fun onError(e: Throwable?) {
                    deliveries += requireNotNull(e)
                }

                override fun onSuccess(cachedImage: DivCachedImage) {
                    deliveries += cachedImage
                }
            }
        val loader =
            mock<DivImageLoader> {
                on { hasSvgSupport() } doReturn true
                on { needLimitBitmapSize() } doReturn true
                on { loadImage(any(), any<DivImageDownloadCallback>()) } doAnswer { invocation ->
                    invocation.getArgument<DivImageDownloadCallback>(1).apply {
                        onError(expectedError)
                        onSuccess(expectedImage)
                    }
                    loadReference
                }
            }

        createWrapper(loader).loadImage(IMAGE_URL, callback)

        assertEquals(listOf(expectedError, expectedImage), deliveries)
    }

    @Test
    fun `empty URL with priority cancels without loading`() {
        val callback = mock<DivImageDownloadCallback>()

        wrapper.loadImage("empty://", callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD)

        verify(callback).onCancel()
        verify(providedLoader, never()).loadImage(any(), any(), any())
    }

    @Test
    fun `SVG fallback uses host network client`() {
        val networkClient = mock<DivNetworkClient>()
        runBlocking { whenever(networkClient.execute(any())).thenThrow(IllegalStateException("stop")) }
        val providedLoader =
            mock<DivImageLoader> {
                on { hasSvgSupport() } doReturn false
            }
        val wrapper = createWrapper(providedLoader, networkClient)

        wrapper.loadImage("https://example.com/image.svg", mock<DivImageDownloadCallback>())
        shadowOf(Looper.getMainLooper()).idle()

        runBlocking { verify(networkClient, timeout(5_000)).execute(any()) }
    }

    private fun createWrapper(
        imageLoader: DivImageLoader,
        networkClient: DivNetworkClient? = null,
    ) = DivImageLoaderWrapper(
        imageLoader,
        ApplicationProvider.getApplicationContext(),
        networkClient,
    )

    private companion object {
        const val IMAGE_URL = "https://example.com/image.png"
    }
}
