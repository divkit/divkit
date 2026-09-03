package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import org.junit.Assert.assertArrayEquals
import org.junit.Test

@OptIn(InternalApi::class)
class WeightedSizeAllocatorTest {

    @Test
    fun `each equally weighted line rounds up independently`() {
        val sizes = resolveWeightedSizes(
            weights = floatArrayOf(1f, 1f, 1f),
            baseSizes = intArrayOf(0, 44, 0),
            minimumSize = 400,
        )

        assertArrayEquals(intArrayOf(134, 134, 134), sizes)
    }

    @Test
    fun `each unequally weighted line rounds up independently`() {
        val sizes = resolveWeightedSizes(
            weights = floatArrayOf(3f, 2f, 1f),
            baseSizes = intArrayOf(0, 0, 0),
            minimumSize = 400,
        )

        assertArrayEquals(intArrayOf(200, 134, 67), sizes)
    }

    @Test
    fun `weighted lines preserve the largest base size ratio`() {
        val sizes = resolveWeightedSizes(
            weights = floatArrayOf(1f, 1f, 1f),
            baseSizes = intArrayOf(0, 44, 0),
            minimumSize = 100,
        )

        assertArrayEquals(intArrayOf(44, 44, 44), sizes)
    }

    @Test
    fun `fixed lines are excluded from weighted distribution`() {
        val sizes = resolveWeightedSizes(
            weights = floatArrayOf(0f, 1f, 1f),
            baseSizes = intArrayOf(40, 0, 0),
            minimumSize = 100,
        )

        assertArrayEquals(intArrayOf(40, 30, 30), sizes)
    }
}
