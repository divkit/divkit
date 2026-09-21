package com.yandex.div.compose.preload

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ImageLoader
import coil3.asImage
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import com.yandex.div.compose.images.ImageRequestFactory
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.color
import com.yandex.div.test.data.container
import com.yandex.div.test.data.gifImage
import com.yandex.div.test.data.image
import com.yandex.div.test.data.imageBackground
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.solidBackground
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CoilImagePreloaderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val capturedUrls = mutableSetOf<String>()
    private var resultProvider: (ImageRequest) -> ImageResult = { request -> successResult(request) }

    private val imageLoader = ImageLoader.Builder(context)
        .components {
            add { chain ->
                capturedUrls.add(chain.request.data.toString())
                resultProvider(chain.request)
            }
        }
        .build()

    private val preloader = CoilImagePreloader(
        imageLoader = imageLoader,
        imageRequestFactory = ImageRequestFactory(
            context = context,
            imageRequestListener = mock(),
        ),
    )

    private val resolver = ExpressionResolver.EMPTY

    @Test
    fun `preloads Image content when preloadRequired is true`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        val result = preloader.preloadImages(
            image(
                imageUrl = imageUrl,
                preloadRequired = true
            ),
            resolver
        )

        assertTrue(result.isSuccessful)
        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `returns false for ErrorResult`() = runTest {
        resultProvider = { request -> errorResult(request) }

        val result = preloader.preloadImages(
            image(
                imageUrl = "https://example.com/img.jpg",
                preloadRequired = true,
            ),
            resolver,
        )

        assertFalse(result.isSuccessful)
    }

    @Test
    fun `loads every selected image when one result fails`() = runTest {
        val failedUrl = "https://example.com/fail.jpg"
        val successfulUrl = "https://example.com/success.jpg"
        resultProvider = { request ->
            if (request.data.toString() == failedUrl) errorResult(request) else successResult(request)
        }

        val result = preloader.preloadImages(
            container(
                backgrounds = listOf(
                    imageBackground(failedUrl, preloadRequired = true),
                    imageBackground(successfulUrl, preloadRequired = true),
                )
            ),
            resolver,
        )

        assertFalse(result.isSuccessful)
        assertEquals(setOf(failedUrl, successfulUrl), capturedUrls)
    }

    @Test
    fun `returns true when no image is selected`() = runTest {
        val result = preloader.preloadImages(
            image(
                imageUrl = "https://example.com/img.jpg",
                preloadRequired = false,
            ),
            resolver,
        )

        assertTrue(result.isSuccessful)
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `propagates image loading cancellation`() = runTest {
        resultProvider = { throw CancellationException("cancelled") }

        val error = assertFailsWith<CancellationException> {
            preloader.preloadImages(
                image(
                    imageUrl = "https://example.com/img.jpg",
                    preloadRequired = true,
                ),
                resolver,
            )
        }

        assertEquals("cancelled", error.message)
    }

    @Test
    fun `does not preload Image when preloadRequired is false`() = runTest {
        preloader.preloadImages(
            image(
                imageUrl = "https://example.com/img.jpg",
                preloadRequired = false
            ),
            resolver
        )

        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `returns true without loading when Image URL is absent`() = runTest {
        val missingUrl: String? = null

        val result = preloader.preloadImages(
            image(
                imageUrl = missingUrl,
                preloadRequired = true,
            ),
            resolver,
        )

        assertTrue(result.isSuccessful)
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `preloads Image when downloadAll ignores preloadRequired`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(
            image(
                imageUrl = imageUrl,
                preloadRequired = false
            ),
            resolver,
            downloadAll = true,
        )

        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `preloads GifImage content when preloadRequired is true`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(
            gifImage(imageUrl, preloadRequired = true),
            resolver
        )

        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `does not preload GifImage when preloadRequired is false`() = runTest {
        preloader.preloadImages(
            gifImage("https://example.com/img.jpg", preloadRequired = false),
            resolver
        )

        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `preloads GifImage when downloadAll ignores preloadRequired`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(
            gifImage(imageUrl, preloadRequired = false),
            resolver,
            downloadAll = true,
        )

        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `preloads Text image when preloadRequired is true`() = runTest {
        val image1Url = "https://example.com/img.jpg"
        val image2Url = "https://example.com/img2.jpg"
        preloader.preloadImages(
            text(
                text = "hello",
                images = listOf(
                    textImage(image1Url, preloadRequired = true),
                    textImage(image2Url, preloadRequired = true),
                    textImage("https://example.com/img3.jpg", preloadRequired = false)
                )
            ),
            resolver
        )

        assertEquals(setOf(image1Url, image2Url), capturedUrls)
    }

    @Test
    fun `does not preload Text with no images`() = runTest {
        preloader.preloadImages(
            text(text = "hello"),
            resolver
        )

        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `preloads image background when preloadRequired is true`() = runTest {
        val image1Url = "https://example.com/img.jpg"
        val image2Url = "https://example.com/img2.jpg"
        preloader.preloadImages(
            container(
                backgrounds = listOf(
                    imageBackground(image1Url, preloadRequired = true),
                    imageBackground(image2Url, preloadRequired = true),
                    imageBackground("https://example.com/img3.jpg", preloadRequired = false),
                )
            ),
            resolver
        )

        assertEquals(setOf(image1Url, image2Url), capturedUrls)
    }

    @Test
    fun `does not preload solid background`() = runTest {
        val div = container(
            backgrounds = listOf(
                solidBackground(color = color(0xFF000000)),
            )
        )
        preloader.preloadImages(div, resolver)
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `other div types do not preload content`() = runTest {
        val result = preloader.preloadImages(separator(), resolver)
        assertTrue(result.isSuccessful)
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `background and content are preloaded together`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        val backgroundUrl = "https://example.com/img2.jpg"
        preloader.preloadImages(
            image(
                background = listOf(
                    imageBackground(backgroundUrl, preloadRequired = true)
                ),
                imageUrl = imageUrl,
                preloadRequired = true
            ),
            resolver
        )

        assertEquals(setOf(imageUrl, backgroundUrl), capturedUrls)
    }

    private fun successResult(request: ImageRequest): SuccessResult = SuccessResult(
        image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImage(),
        request = request,
    )

    private fun errorResult(
        request: ImageRequest,
        throwable: Throwable = IllegalStateException("load failed"),
    ): ErrorResult = ErrorResult(
        image = null,
        request = request,
        throwable = throwable,
    )
}
