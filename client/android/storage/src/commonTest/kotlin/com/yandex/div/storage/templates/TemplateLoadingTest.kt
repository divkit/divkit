package com.yandex.div.storage.templates

import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateLoadingTest {
    @Test
    fun `partially cached request reads only the missing hashes in order`() {
        val hashes = linkedSetOf("missing-first", "cached", "missing-second")

        val queriedHashes = loadMissingTemplates(hashes, setOf("cached", "other-group")) {
            it.toList()
        }

        assertEquals(listOf("missing-first", "missing-second"), queriedHashes)
        assertEquals(listOf("missing-first", "cached", "missing-second"), hashes.toList())
    }
}
