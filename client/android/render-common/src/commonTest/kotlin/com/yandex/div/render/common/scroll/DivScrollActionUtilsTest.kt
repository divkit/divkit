package com.yandex.div.render.common.scroll

import com.yandex.div.render.common.scroll.DivScrollActionUtils.Overflow
import kotlin.test.Test
import kotlin.test.assertEquals

class DivScrollActionUtilsTest {

    @Test
    fun `clamped item has no target when item count is nonpositive`() {
        for (count in listOf(0, -1, Int.MIN_VALUE)) {
            val target = DivScrollActionUtils.targetItem(
                nearestItem = 2, itemCount = count, step = -3, overflow = Overflow.CLAMP,
            )

            assertEquals(-1, target, "count=$count")
        }
    }

    @Test
    fun `cyclic item has no target when item count is nonpositive`() {
        for (count in listOf(0, -1, Int.MIN_VALUE)) {
            val target = DivScrollActionUtils.targetItem(
                nearestItem = 2, itemCount = count, step = -3, overflow = Overflow.RING,
            )

            assertEquals(-1, target, "count=$count")
        }
    }

    @Test
    fun `clamped offset has zero target when scroll range is nonpositive`() {
        for (range in listOf(0, -1, Int.MIN_VALUE)) {
            val target = DivScrollActionUtils.targetOffset(
                scrollOffset = 20, scrollRange = range, offset = -30, overflow = Overflow.CLAMP,
            )

            assertEquals(0, target, "range=$range")
        }
    }

    @Test
    fun `cyclic offset has zero target when scroll range is nonpositive`() {
        for (range in listOf(0, -1, Int.MIN_VALUE)) {
            val target = DivScrollActionUtils.targetOffset(
                scrollOffset = 20, scrollRange = range, offset = -30, overflow = Overflow.RING,
            )

            assertEquals(0, target, "range=$range")
        }
    }

    @Test
    fun `target item clamps directional steps to item bounds`() {
        for (case in itemCases) {
            val target = DivScrollActionUtils.targetItem(
                case.nearest, case.count, case.step, Overflow.CLAMP,
            )

            assertEquals(case.clamp, target, case.toString())
        }
    }

    @Test
    fun `target item wraps directional steps around item bounds`() {
        for (case in itemCases) {
            val target = DivScrollActionUtils.targetItem(
                case.nearest, case.count, case.step, Overflow.RING,
            )

            assertEquals(case.ring, target, case.toString())
        }
    }

    @Test
    fun `target offset clamps to the inclusive scroll range`() {
        for (case in offsetCases) {
            val target = DivScrollActionUtils.targetOffset(
                case.from, case.range, case.offset, Overflow.CLAMP,
            )

            assertEquals(case.clamp, target, case.toString())
        }
    }

    @Test
    fun `target offset wraps at the scroll range`() {
        for (case in offsetCases) {
            val target = DivScrollActionUtils.targetOffset(
                case.from, case.range, case.offset, Overflow.RING,
            )

            assertEquals(case.ring, target, case.toString())
        }
    }

    @Test
    fun `aligned anchors select the adjacent item according to direction and reversal`() {
        val cases = listOf(
            AnchorCase(direction = 1, reversed = false, expected = 1),
            AnchorCase(direction = 0, reversed = false, expected = 1),
            AnchorCase(direction = -1, reversed = false, expected = -1),
            AnchorCase(direction = 1, reversed = true, expected = -1),
            AnchorCase(direction = 0, reversed = true, expected = -1),
            AnchorCase(direction = -1, reversed = true, expected = 1),
        )

        for (case in cases) {
            val offset = DivScrollActionUtils.nearestItemIndexOffset(
                childAnchor = 10,
                parentAnchor = 10,
                direction = case.direction,
                reversed = case.reversed,
            )

            assertEquals(case.expected, offset, case.toString())
        }
    }

    @Test
    fun `unaligned anchors select only items in the requested direction regardless of reversal`() {
        val cases = listOf(
            UnalignedCase(child = 11, direction = 1, reversed = false, expected = 0),
            UnalignedCase(child = 11, direction = 1, reversed = true, expected = 0),
            UnalignedCase(child = 11, direction = 0, reversed = false, expected = 0),
            UnalignedCase(child = 11, direction = 0, reversed = true, expected = 0),
            UnalignedCase(child = 11, direction = -1, reversed = false, expected = null),
            UnalignedCase(child = 11, direction = -1, reversed = true, expected = null),
            UnalignedCase(child = 9, direction = 1, reversed = false, expected = null),
            UnalignedCase(child = 9, direction = 1, reversed = true, expected = null),
            UnalignedCase(child = 9, direction = 0, reversed = false, expected = null),
            UnalignedCase(child = 9, direction = 0, reversed = true, expected = null),
            UnalignedCase(child = 9, direction = -1, reversed = false, expected = 0),
            UnalignedCase(child = 9, direction = -1, reversed = true, expected = 0),
        )

        for (case in cases) {
            val offset = DivScrollActionUtils.nearestItemIndexOffset(
                childAnchor = case.child,
                parentAnchor = 10,
                direction = case.direction,
                reversed = case.reversed,
            )

            assertEquals(case.expected, offset, case.toString())
        }
    }

    private val itemCases = listOf(
        ItemCase(nearest = 2, count = 5, step = 1, clamp = 2, ring = 2),
        ItemCase(nearest = 2, count = 5, step = -1, clamp = 2, ring = 2),
        ItemCase(nearest = 2, count = 5, step = 0, clamp = 2, ring = 2),
        ItemCase(nearest = 2, count = 5, step = 3, clamp = 4, ring = 4),
        ItemCase(nearest = 2, count = 5, step = -3, clamp = 0, ring = 0),
        ItemCase(nearest = 4, count = 5, step = 2, clamp = 4, ring = 0),
        ItemCase(nearest = 0, count = 5, step = -2, clamp = 0, ring = 4),
        ItemCase(nearest = -1, count = 5, step = 0, clamp = 0, ring = 4),
        ItemCase(nearest = 5, count = 5, step = 0, clamp = 4, ring = 0),
        ItemCase(nearest = 2, count = 5, step = 13, clamp = 4, ring = 4),
        ItemCase(nearest = 2, count = 5, step = -13, clamp = 0, ring = 0),
        ItemCase(nearest = 0, count = 1, step = -10, clamp = 0, ring = 0),
        ItemCase(nearest = Int.MAX_VALUE, count = 5, step = 2, clamp = 0, ring = 2),
        ItemCase(nearest = Int.MIN_VALUE, count = 5, step = -2, clamp = 4, ring = 2),
    )

    private val offsetCases = listOf(
        OffsetCase(from = 20, range = 100, offset = 30, clamp = 50, ring = 50),
        OffsetCase(from = 20, range = 100, offset = -20, clamp = 0, ring = 0),
        OffsetCase(from = 20, range = 100, offset = 80, clamp = 100, ring = 0),
        OffsetCase(from = 20, range = 100, offset = 90, clamp = 100, ring = 10),
        OffsetCase(from = 20, range = 100, offset = -30, clamp = 0, ring = 90),
        OffsetCase(from = 20, range = 100, offset = -220, clamp = 0, ring = 0),
        OffsetCase(from = 20, range = 100, offset = 290, clamp = 100, ring = 10),
        OffsetCase(from = 20, range = 100, offset = 0, clamp = 20, ring = 20),
        OffsetCase(from = 100, range = 100, offset = 0, clamp = 100, ring = 0),
        OffsetCase(from = -1, range = 100, offset = 0, clamp = 0, ring = 99),
        OffsetCase(from = 0, range = 1, offset = 1, clamp = 1, ring = 0),
        OffsetCase(from = Int.MAX_VALUE, range = 100, offset = 1, clamp = 0, ring = 52),
        OffsetCase(from = Int.MIN_VALUE, range = 100, offset = -1, clamp = 100, ring = 47),
    )

    private data class ItemCase(
        val nearest: Int,
        val count: Int,
        val step: Int,
        val clamp: Int,
        val ring: Int,
    )

    private data class OffsetCase(
        val from: Int,
        val range: Int,
        val offset: Int,
        val clamp: Int,
        val ring: Int,
    )

    private data class AnchorCase(val direction: Int, val reversed: Boolean, val expected: Int)

    private data class UnalignedCase(
        val child: Int,
        val direction: Int,
        val reversed: Boolean,
        val expected: Int?,
    )
}
