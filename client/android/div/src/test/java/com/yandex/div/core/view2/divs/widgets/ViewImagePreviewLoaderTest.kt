package com.yandex.div.core.view2.divs.widgets

import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ViewImagePreviewLoaderTest {
    private val callback = argumentCaptor<DivImageDownloadCallback>()
    private val reference = mock<LoadReference>()
    private val imageLoader = mock<DivImageLoader> {
        on { loadImage(eq(PREVIEW_URL), callback.capture()) } doReturn reference
    }
    private val loader = ViewImagePreviewLoader(imageLoader)
    private val image = DivCachedImage.Bitmap(createBitmap(2, 2), BitmapSource.NETWORK)
    private val onLoaded = mock<(DivCachedImage, ViewImagePreviewLoader.Request) -> Unit>()
    private val onError = mock<(Throwable?, ViewImagePreviewLoader.Request) -> Unit>()

    @Test
    fun `success delivers image and current request`() {
        val request = loader.load(PREVIEW_URL, onLoaded, onError)

        callback.firstValue.onSuccess(image)

        verify(onLoaded).invoke(image, request)
        assertTrue(request.isCurrent())
    }

    @Test
    fun `replacement cancels previous request and ignores its late callbacks`() {
        whenever(
            imageLoader.loadImage(eq(REPLACEMENT_URL), any<DivImageDownloadCallback>())
        ) doReturn mock()
        loader.load(PREVIEW_URL, onLoaded, onError)
        val error = IllegalStateException("Late error")

        loader.load(REPLACEMENT_URL, mock(), mock())
        callback.firstValue.onSuccess(image)
        callback.firstValue.onError(error)

        verify(reference).cancel()
        verifyNoInteractions(onLoaded, onError)
    }

    @Test
    fun `cancel invalidates request and cancels loading`() {
        val request = loader.load(PREVIEW_URL, onLoaded, onError)

        loader.cancel()

        assertFalse(request.isCurrent())
        verify(reference).cancel()
    }

    @Test
    fun `cancel ignores late callbacks`() {
        loader.load(PREVIEW_URL, onLoaded, onError)
        val error = IllegalStateException("Late error")
        loader.cancel()

        callback.firstValue.onSuccess(image)
        callback.firstValue.onError(error)

        verifyNoInteractions(onLoaded, onError)
    }

    @Test
    fun `current error is delivered`() {
        val error = IllegalStateException("HTTP 404")
        val request = loader.load(PREVIEW_URL, onLoaded, onError)

        callback.firstValue.onError(error)

        verify(onError).invoke(error, request)
        verifyNoInteractions(onLoaded)
    }

    @Test
    fun `synchronous success sees current request`() {
        whenever(
            imageLoader.loadImage(eq(PREVIEW_URL), any<DivImageDownloadCallback>())
        ) doAnswer {
            it.getArgument<DivImageDownloadCallback>(1).onSuccess(image)
            reference
        }

        val request = loader.load(
            PREVIEW_URL,
            onLoaded = { loadedImage, request ->
                assertTrue(request.isCurrent())
                onLoaded(loadedImage, request)
            },
            onError = onError,
        )

        verify(onLoaded).invoke(image, request)
    }

    @Test
    fun `cancel during synchronous success cancels reference returned afterwards`() {
        whenever(
            imageLoader.loadImage(eq(PREVIEW_URL), any<DivImageDownloadCallback>())
        ) doAnswer {
            it.getArgument<DivImageDownloadCallback>(1).onSuccess(image)
            reference
        }

        val request = loader.load(
            PREVIEW_URL,
            onLoaded = { _, _ -> loader.cancel() },
            onError = onError,
        )

        assertFalse(request.isCurrent())
        verify(reference).cancel()
    }

    @Test
    fun `cancelling old registration keeps replacement current`() {
        val replacementReference = mock<LoadReference>()
        whenever(
            imageLoader.loadImage(eq(REPLACEMENT_URL), any<DivImageDownloadCallback>())
        ) doReturn replacementReference
        val oldRequest = loader.load(PREVIEW_URL, onLoaded, onError)
        val currentRequest = loader.load(REPLACEMENT_URL, mock(), mock())

        oldRequest.cancel()

        assertFalse(oldRequest.isCurrent())
        assertTrue(currentRequest.isCurrent())
        verifyNoInteractions(replacementReference)
    }

    private companion object {
        const val PREVIEW_URL = "https://divkit.tech/preview.png"
        const val REPLACEMENT_URL = "https://divkit.tech/replacement.png"
    }
}
