package com.yandex.div.compose.images

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.Size
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ImageRequestFactoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val factory = ImageRequestFactory(context, mock())

    @Test
    fun `limits requested image size to display`() = runTest {
        val request = factory.build(
            ImageRequestParams(
                data = "https://divkit.tech/image.png",
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
}
