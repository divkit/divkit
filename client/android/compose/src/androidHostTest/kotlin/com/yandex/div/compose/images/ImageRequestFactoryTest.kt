package com.yandex.div.compose.images

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.ImageRequest
import coil3.request.Options
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.Size
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.di.DivContextPlatformModule
import com.yandex.div.compose.divConfiguration
import kotlinx.coroutines.test.runTest
import okio.Buffer
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class ImageRequestFactoryTest {

    @get:Rule
    val rule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val factory = ImageRequestFactory(
        context = context,
        imageRequestListener = mock(),
        maxDisplaySize = DivContextPlatformModule.provideMaxDisplaySize(context),
    )

    @Test
    fun `limits requested image size to display`() = runTest {
        val request = factory.build(
            ImageRequestParams(
                data = IMAGE_URL,
                limitToDisplaySize = true,
            )
        )

        val maxDisplaySize = context.resources.displayMetrics.let {
            max(it.widthPixels, it.heightPixels)
        }
        assertEquals(
            Size(width = maxDisplaySize, height = maxDisplaySize),
            request.sizeResolver.size(),
        )
        assertEquals(Scale.FIT, request.scale)
        assertEquals(Precision.INEXACT, request.precision)
    }

    @Test
    fun `Android decoder recognizes the synchronous request flag`() {
        val request = factory.build(ImageRequestParams(data = IMAGE_URL, synchronous = true))
        val options = Options(context = context, extras = request.extras)

        ImageSource(Buffer(), request.fileSystem).use { source ->
            val result = SourceFetchResult(source, mimeType = null, dataSource = DataSource.MEMORY)
            val decoder = UnthrottledImageDecoderFactory.create(result, options, mock())

            assertNotNull(decoder)
        }
    }

    @Test
    fun `Android decoder returns null for asynchronous requests`() {
        val request = factory.build(ImageRequestParams(data = IMAGE_URL, synchronous = false))
        val options = Options(context = context, extras = request.extras)

        ImageSource(Buffer(), request.fileSystem).use { source ->
            val result = SourceFetchResult(source, mimeType = null, dataSource = DataSource.MEMORY)

            assertNull(UnthrottledImageDecoderFactory.create(result, options, mock()))
        }
    }

    @Test
    fun `recomposition reuses the request for equal parameters`() {
        val divContext = DivContext(context, divConfiguration {})
        val generation = mutableIntStateOf(0)
        val requests = mutableMapOf<Int, ImageRequest>()

        rule.setContent {
            CompositionLocalProvider(LocalDivContext provides divContext) {
                val currentGeneration = generation.intValue
                val request = rememberImageRequest(ImageRequestParams(data = IMAGE_URL))
                SideEffect { requests[currentGeneration] = request }
            }
        }

        rule.runOnIdle { generation.intValue++ }

        rule.runOnIdle {
            val initial = requests.getValue(0)
            assertSame(initial, requests.getValue(1))
        }
    }

    @Test
    fun `recomposition replaces the listener when DivContext changes`() {
        val firstContext = DivContext(context, divConfiguration {})
        val secondContext = DivContext(context, divConfiguration {})
        val currentContext = mutableStateOf(firstContext)
        val params = ImageRequestParams(data = IMAGE_URL)
        lateinit var request: ImageRequest

        rule.setContent {
            CompositionLocalProvider(LocalContext provides currentContext.value) {
                val currentRequest = rememberImageRequest(params)
                SideEffect { request = currentRequest }
            }
        }

        rule.runOnIdle {
            assertSame(firstContext.component.imageRequestFactory.build(params).listener, request.listener)
            currentContext.value = secondContext
        }

        rule.runOnIdle {
            assertSame(secondContext.component.imageRequestFactory.build(params).listener, request.listener)
        }
    }

    private companion object {
        const val IMAGE_URL = "https://divkit.tech/image.png"
    }
}
