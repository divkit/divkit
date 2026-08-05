package com.yandex.div.compose.images

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class ImagePreviewDecoderTest {

    private val reporter = TestReporter()
    private val decoder = ImagePreviewDecoder(reporter)

    @Test
    fun `decodes plain Base64`() {
        val preview = decoder.decodePreview("SGVsbG8=")

        assertContentEquals("Hello".encodeToByteArray(), preview)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `decodes Base64 data URL`() {
        val preview = decoder.decodePreview("data:image/png;base64,SGVsbG8=")

        assertContentEquals("Hello".encodeToByteArray(), preview)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `reports invalid Base64 and returns null`() {
        val reporter = TestReporter().apply { failOnError = false }
        val preview = ImagePreviewDecoder(reporter).decodePreview("=")

        assertNull(preview)
        assertEquals(listOf("Preview doesn't contain base64 image"), reporter.errors)
    }
}
