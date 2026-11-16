package com.yandex.div.compose.images

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class ImagePreviewDecoderTest {

    private val reporter = TestReporter()
    private val decoder = ImagePreviewDecoder(reporter)

    @Test
    fun `decodes plain Base64`() {
        val preview = decoder.decodePreview("SGVsbG8=")

        val bytes = assertIs<ImagePreview.Base64>(preview).bytes
        assertContentEquals("Hello".encodeToByteArray(), bytes)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `decodes Base64 data URL`() {
        val preview = decoder.decodePreview("data:image/png;base64,SGVsbG8=")

        val bytes = assertIs<ImagePreview.Base64>(preview).bytes
        assertContentEquals("Hello".encodeToByteArray(), bytes)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `returns URL without Base64 decoding`() {
        val preview = decoder.decodePreview("https://divkit.tech/preview.png")

        assertEquals(ImagePreview.Url("https://divkit.tech/preview.png".toUri()), preview)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `decodes Base64 without padding`() {
        val preview = decoder.decodePreview("SGVsbG8")

        val bytes = assertIs<ImagePreview.Base64>(preview).bytes
        assertContentEquals("Hello".encodeToByteArray(), bytes)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `decodes Base64 with whitespace and line breaks`() {
        val preview = decoder.decodePreview("\tSGVs\r\nbG8= ")

        val bytes = assertIs<ImagePreview.Base64>(preview).bytes
        assertContentEquals("Hello".encodeToByteArray(), bytes)
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `reports non-zero pad bits and returns null`() {
        reporter.failOnError = false

        val preview = decoder.decodePreview("Zh==")

        assertNull(preview)
        assertEquals(listOf("Failed to decode Base64 image preview"), reporter.errors)
    }

    @Test
    fun `reports invalid Base64 and returns null`() {
        val reporter = TestReporter().apply { failOnError = false }
        val preview = ImagePreviewDecoder(reporter).decodePreview("=")

        assertNull(preview)
        assertEquals(listOf("Failed to decode Base64 image preview"), reporter.errors)
    }
}
