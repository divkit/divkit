package com.yandex.div.compose.images

import android.content.Context
import androidx.core.graphics.createBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.Uri
import coil3.asImage
import coil3.decode.DataSource
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import com.yandex.div.compose.di.DivContextPlatformModule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

private const val IMAGE_BODY = "image"

@RunWith(AndroidJUnit4::class)
class ImageLoaderFactoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val platformConfiguration = object : PlatformImageLoaderConfiguration {
        override fun createImageLoaderBuilder(): ImageLoader.Builder = ImageLoader.Builder(context)

        override fun applyComponents(builder: ComponentRegistry.Builder) {
            builder.add(
                Decoder.Factory { result, _, _ ->
                    Decoder {
                        val body = result.source.source().readUtf8()
                        check(body == IMAGE_BODY) { "Unexpected image data" }
                        DecodeResult(createBitmap(1, 1).asImage(), isSampled = false)
                    }
                }
            )
        }
    }

    @Test
    fun `host fetcher takes precedence over the built-in network fetcher`() = runTest {
        val hostImage = createBitmap(1, 1).asImage()
        val hostConfiguration = object : ImageLoaderConfiguration {
            override fun applyComponents(builder: ComponentRegistry.Builder) {
                builder.add(
                    Fetcher.Factory<Uri> { _, _, _ ->
                        Fetcher {
                            ImageFetchResult(
                                image = hostImage,
                                isSampled = false,
                                dataSource = DataSource.NETWORK,
                            )
                        }
                    }
                )
            }
        }
        MockWebServer().use { server ->
            val result = createImageLoader(hostConfiguration)
                .execute(imageRequest(server.url("/host.png").toString()))

            assertSame(hostImage, assertSuccess(result).image)
            assertEquals(0, server.requestCount)
        }
    }

    @Test
    fun `built-in network fetcher follows 300 redirect`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(300).setHeader("Location", "/image.png"))
            server.enqueue(MockResponse().setBody(IMAGE_BODY))

            val request = imageRequest(server.url("/redirect").toString())
            val result = createImageLoader().execute(request)

            assertSuccess(result)
            assertEquals(2, server.requestCount)
            assertEquals(listOf("/redirect", "/image.png"), List(2) { server.takeRequest().path })
        }
    }

    @Test
    fun `body cut off by the server fails the load and is not cached`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(
                MockResponse()
                    .setBody(IMAGE_BODY.repeat(100))
                    .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)
            )
            server.enqueue(MockResponse().setBody(IMAGE_BODY))
            val imageLoader = createImageLoader()
            val request = imageRequest(server.url("/cut-off.png").toString())

            assertIs<ErrorResult>(imageLoader.execute(request))
            assertSuccess(imageLoader.execute(request))
            assertEquals(2, server.requestCount)
        }
    }

    @Test
    fun `repeated cookie headers are joined with the cookie separator`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody(IMAGE_BODY))
            val headers = NetworkHeaders.Builder()
                .add("Cookie", "first=1")
                .add("Cookie", "second=2")
                .build()
            val request = ImageRequest.Builder(context)
                .data(server.url("/cookie.png").toString())
                .httpHeaders(headers)
                .build()

            assertSuccess(createImageLoader().execute(request))
            assertEquals("first=1; second=2", server.takeRequest().getHeader("Cookie"))
        }
    }

    private fun createImageLoader(
        configuration: ImageLoaderConfiguration = object : ImageLoaderConfiguration {},
    ): ImageLoader {
        return ImageLoaderFactory(
            externalImageLoaderConfiguration = configuration,
            platformImageLoaderConfiguration = platformConfiguration,
            networkClient = lazy { KtorImageNetworkClient(DivContextPlatformModule.provideImageHttpClient()) },
        ).createImageLoader()
    }

    private fun imageRequest(url: String) = ImageRequest.Builder(context).data(url).build()

    private fun assertSuccess(result: ImageResult): SuccessResult {
        return assertIs<SuccessResult>(result, (result as? ErrorResult)?.throwable?.toString())
    }
}
