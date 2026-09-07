package com.yandex.div.compose.views.indicator

import kotlin.test.Test
import kotlin.test.assertEquals

class InfinitePagerPositionTest {

    @Test
    fun `backward scroll from first page starts at last page`() {
        val (page, offset) = normalizePagerPosition(
            rawPage = 0,
            rawOffset = 0.3f,
            itemsCount = 5,
            infiniteScroll = true,
        )

        assertEquals(4, page)
        assertEquals(0.7f, offset, absoluteTolerance = 1e-5f)
    }
}
