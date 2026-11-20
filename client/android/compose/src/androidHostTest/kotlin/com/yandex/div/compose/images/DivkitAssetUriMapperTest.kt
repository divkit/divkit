package com.yandex.div.compose.images

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ComponentRegistry
import coil3.request.Options
import coil3.toUri
import com.yandex.div.compose.TestReporter
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class DivkitAssetUriMapperTest {

    private val components = ComponentRegistry.Builder()
        .add(DivkitAssetUriMapper())
        .build()

    private val options = Options(ApplicationProvider.getApplicationContext())

    @Test
    fun `maps Android asset URI to bundled asset`() {
        val uri = Uri.parse("divkit-asset://images/preview%20one.png")

        val mapped = components.map(uri, options)

        assertEquals("file:///android_asset/divkit/images/preview%20one.png", mapped)
    }

    @Test
    fun `maps asset preview URI to bundled asset`() {
        val preview = assertIs<ImagePreview.Url>(
            ImagePreviewDecoder(TestReporter()).decodePreview(
                "divkit-asset://images/preview%20one.png"
            )
        )

        val mapped = components.map(preview.uri, options)

        assertEquals("file:///android_asset/divkit/images/preview%20one.png", mapped)
    }

    @Test
    fun `leaves network URI unchanged`() {
        val uri = "https://divkit.tech/preview.png".toUri()

        val mapped = components.map(uri, options)

        assertSame(uri, mapped)
    }
}
