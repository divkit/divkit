package com.yandex.div.internal.core

import kotlin.test.Test
import kotlin.test.assertContentEquals

class GridTrackSizesTest {

    @Test
    fun `single pixel is preserved across empty tracks`() {
        val sizes = resolveGridTrackSizes(
            count = 2,
            items = listOf(GridItemMeasurement(0, 1, 1, 2, 0f)),
            minimumSize = 0,
        )

        assertContentEquals(intArrayOf(1, 0), sizes)
    }

    @Test
    fun `remainder is distributed only between unused tracks in the span`() {
        val sizes = resolveGridTrackSizes(
            count = 4,
            items = listOf(
                GridItemMeasurement(0, 2, 2, 1, 0f),
                GridItemMeasurement(1, 2, 2, 1, 0f),
                GridItemMeasurement(1, 5, 5, 3, 0f),
            ),
            minimumSize = 0,
        )

        assertContentEquals(intArrayOf(2, 2, 2, 1), sizes)
    }

    @Test
    fun `remainder is distributed between occupied tracks`() {
        val sizes = resolveGridTrackSizes(
            count = 3,
            items = listOf(
                GridItemMeasurement(0, 2, 2, 1, 0f),
                GridItemMeasurement(1, 2, 2, 1, 0f),
                GridItemMeasurement(2, 2, 2, 1, 0f),
                GridItemMeasurement(0, 8, 8, 3, 0f),
            ),
            minimumSize = 0,
        )

        assertContentEquals(intArrayOf(3, 3, 2), sizes)
    }

    @Test
    fun `divisible extra size is distributed equally`() {
        val sizes = resolveGridTrackSizes(
            count = 3,
            items = listOf(
                GridItemMeasurement(0, 2, 2, 1, 0f),
                GridItemMeasurement(1, 2, 2, 1, 0f),
                GridItemMeasurement(2, 2, 2, 1, 0f),
                GridItemMeasurement(0, 9, 9, 3, 0f),
            ),
            minimumSize = 0,
        )

        assertContentEquals(intArrayOf(3, 3, 3), sizes)
    }
}
