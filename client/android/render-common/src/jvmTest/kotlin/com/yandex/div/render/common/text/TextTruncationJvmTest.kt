package com.yandex.div.render.common.text

import com.yandex.div.core.annotations.InternalApi
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalApi::class)
class TextTruncationJvmTest {

    @Test
    fun `uses locale line boundaries for word truncation`() {
        val truncation = TextTruncation("first second", Locale.US)

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 10, byWord = true) { true }

        assertEquals(5, offset)
    }

    @Test
    fun `protects emoji clusters with Java boundaries`() {
        val truncation = TextTruncation("ok 👨‍👩‍👧‍👦 tail", Locale.US)

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 8, byWord = false) { true }

        assertEquals(3, offset)
    }
}
