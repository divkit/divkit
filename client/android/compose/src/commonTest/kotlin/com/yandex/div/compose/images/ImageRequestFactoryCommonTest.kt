package com.yandex.div.compose.images

import coil3.Bitmap
import coil3.PlatformContext
import coil3.getExtra
import coil3.request.transformations
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.Size
import coil3.size.SizeResolver
import coil3.transform.Transformation
import com.yandex.div.compose.TestReporter
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal expect fun imageRequestTestContext(): PlatformContext

class ImageRequestFactoryCommonTest {

    private val context = imageRequestTestContext()
    private val listener = ImageRequestListener(object : ImageLoaderConfiguration {}, TestReporter())
    private val factory = ImageRequestFactory(context, listener, MAX_DISPLAY_SIZE)

    @Test
    fun `ordinary request preserves data listener and Coil defaults`() {
        val request = factory.build(ImageRequestParams(data = IMAGE_URL))

        assertSame(context, request.context)
        assertEquals(IMAGE_URL, request.data)
        assertSame(listener, request.listener)
        assertEquals(SizeResolver.ORIGINAL, request.sizeResolver)
        assertNull(request.defined.scale)
        assertNull(request.defined.precision)
        assertFalse(request.getExtra(unthrottledImageDecodeKey))
        assertNull(request.defined.decoderCoroutineContext)
    }

    @Test
    fun `request keeps transformations in their original order`() {
        val transformations = listOf(TestTransformation("first"), TestTransformation("second"))

        val request = factory.build(ImageRequestParams(data = IMAGE_URL, transformations = transformations))

        assertEquals(transformations, request.transformations)
    }

    @Test
    fun `display size limit uses fit and inexact precision`() {
        val request = factory.build(ImageRequestParams(data = IMAGE_URL, limitToDisplaySize = true))

        assertEquals(SizeResolver(Size(MAX_DISPLAY_SIZE, MAX_DISPLAY_SIZE)), request.sizeResolver)
        assertEquals(Scale.FIT, request.defined.scale)
        assertEquals(Precision.INEXACT, request.defined.precision)
    }

    @Test
    fun `synchronous request enables unthrottled decoding and inherits the caller context`() {
        val request = factory.build(ImageRequestParams(data = IMAGE_URL, synchronous = true))

        assertTrue(request.getExtra(unthrottledImageDecodeKey))
        assertSame(EmptyCoroutineContext, request.defined.interceptorCoroutineContext)
        assertSame(EmptyCoroutineContext, request.defined.fetcherCoroutineContext)
        assertSame(EmptyCoroutineContext, request.defined.decoderCoroutineContext)
    }

    private class TestTransformation(override val cacheKey: String) : Transformation() {
        override suspend fun transform(input: Bitmap, size: Size): Bitmap = input
    }

    private companion object {
        const val IMAGE_URL = "https://divkit.tech/image.png"
        const val MAX_DISPLAY_SIZE = 512
    }
}
