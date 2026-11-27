package com.yandex.div.compose.images

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.graphics.toPixelMap
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CoilSkikoCompatibilityTest {

    @OptIn(ExperimentalCoilApi::class)
    @Test
    fun decodesAndDrawsPng() = runBlocking {
        val context = PlatformContext.INSTANCE
        val loader = ImageLoader.Builder(context)
            .mainCoroutineContext(Dispatchers.Unconfined)
            .build()
        try {
            val request = ImageRequest.Builder(context)
                .data(Base64.decode(RED_PIXEL_PNG))
                .build()
            val result = assertIs<SuccessResult>(loader.execute(request))
            assertEquals(1, result.image.width)
            assertEquals(1, result.image.height)

            val target = ImageBitmap(1, 1)
            result.image.draw(Canvas(target).skiaCanvas)

            assertEquals(Color.Red, target.toPixelMap()[0, 0])
        } finally {
            loader.shutdown()
        }
    }
}

private const val RED_PIXEL_PNG =
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGP4z8DwHwAFAAH/iZk9HQAAAABJRU5ErkJggg=="
