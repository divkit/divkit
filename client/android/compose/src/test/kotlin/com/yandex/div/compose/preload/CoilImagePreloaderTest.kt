package com.yandex.div.compose.preload

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ImageLoader
import coil3.asImage
import coil3.request.SuccessResult
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.images.ImageRequestFactory
import com.yandex.div.compose.images.ImageRequestListener
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.color
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.gifImage
import com.yandex.div.test.data.image
import com.yandex.div.test.data.imageBackground
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.solidBackground
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textImage
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class CoilImagePreloaderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val capturedUrls = mutableSetOf<String>()

    private val imageLoader = ImageLoader.Builder(context)
        .components {
            add { chain ->
                capturedUrls.add(chain.request.data.toString())
                SuccessResult(
                    image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImage(),
                    request = chain.request,
                )
            }
        }
        .build()

    private val preloader = CoilImagePreloader(
        imageLoader = imageLoader,
        imageRequestFactory = ImageRequestFactory(
            context = context,
            imageRequestListener = ImageRequestListener(TestReporter()),
        ),
    )

    private val resolver = ExpressionResolver.EMPTY

    @Test
    fun `preloads Div Image content when preloadRequired is true`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(preloadedImage(imageUrl), resolver)
        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `does not preload Div Image when preloadRequired is false`() = runTest {
        preloader.preloadImages(
            preloadedImage("https://example.com/img.jpg", preloadRequired = false),
            resolver
        )
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `preloads Div Image when downloadAll ignores preloadRequired`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(
            preloadedImage(imageUrl, preloadRequired = false),
            resolver,
            downloadAll = true,
        )
        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `preloads Div GifImage content when preloadRequired is true`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(
            gifImage(
                constant(imageUrl.toUri()),
                preloadRequired = constant(true)
            ),
            resolver
        )
        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `does not preload Div GifImage when preloadRequired is false`() = runTest {
        preloader.preloadImages(
            gifImage(
                constant("https://example.com/img.jpg".toUri()),
                preloadRequired = constant(false)
            ),
            resolver
        )
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `preloads Div GifImage when downloadAll ignores preloadRequired`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        preloader.preloadImages(
            gifImage(
                constant(imageUrl.toUri()),
                preloadRequired = constant(false)
            ),
            resolver,
            downloadAll = true,
        )
        assertEquals(imageUrl, capturedUrls.single())
    }

    @Test
    fun `preloads Div Text image when preloadRequired is true`() = runTest {
        val image1Url = "https://example.com/img.jpg"
        val image2Url = "https://example.com/img2.jpg"
        val div = text(
            text = "hello",
            images = listOf(
                preloadedTextImages(image1Url),
                preloadedTextImages(image2Url),
                preloadedTextImages(url = "https://example.com/img3.jpg", preloadRequired = false)
            )
        )
        preloader.preloadImages(div, resolver)
        assertEquals(setOf(image1Url, image2Url), capturedUrls)
    }

    @Test
    fun `does not preload Div Text with no images`() = runTest {
        preloader.preloadImages(text(text = "hello"), resolver)
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `preloads image background when preloadRequired is true`() = runTest {
        val image1Url = "https://example.com/img.jpg"
        val image2Url = "https://example.com/img2.jpg"
        val div = container(
            backgrounds = listOf(
                preloadedImageBackground(image1Url),
                preloadedImageBackground(image2Url),
                preloadedImageBackground(
                    url = "https://example.com/img3.jpg",
                    preloadRequired = false,
                ),
            )
        )
        preloader.preloadImages(div, resolver)
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
        preloader.preloadImages(separator(), resolver)
        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `background and content are preloaded together`() = runTest {
        val imageUrl = "https://example.com/img.jpg"
        val backgroundUrl = "https://example.com/img2.jpg"
        val div = image(
            imageUrl = constant(imageUrl.toUri()),
            preloadRequired = constant(true),
            background = listOf(
                preloadedImageBackground(backgroundUrl)
            ),
        )
        preloader.preloadImages(div, resolver)
        assertEquals(setOf(imageUrl, backgroundUrl), capturedUrls)
    }

    private fun preloadedImageBackground(
        url: String,
        preloadRequired: Boolean = true,
    ) = imageBackground(
        imageUrl = constant(url.toUri()),
        preloadRequired = constant(preloadRequired),
    )

    private fun preloadedImage(url: String, preloadRequired: Boolean = true) = image(
        imageUrl = constant(url.toUri()),
        preloadRequired = constant(preloadRequired),
    )

    private fun preloadedTextImages(url: String, preloadRequired: Boolean = true) = textImage(
        url = constant(url.toUri()),
        preloadRequired = constant(preloadRequired),
    )
}
