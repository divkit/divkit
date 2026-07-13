package com.yandex.div.core.view2.items

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.mock

@RunWith(Parameterized::class)
internal class RingOverflowItemStrategyTest(private val testCase: TestCase) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<TestCase> {
            val itemCount = 5
            val edgeCase1 = TestCase(nearestItem = 0, itemCount = itemCount, nextItem = 1, previousItem = itemCount - 1)
            val edgeCase2 = TestCase(
                nearestItem = itemCount,
                itemCount = itemCount,
                nextItem = 1,
                previousItem = itemCount - 1
            )
            return baseOverflowCases(itemCount, edgeCase1, edgeCase2)
        }
    }

    @Test
    fun `next item`() {
        val underTest = OverflowItemStrategy.Ring(
            testCase.nearestItem,
            testCase.itemCount,
            scrollRange = 0,
            scrollOffset = 0,
            metrics = mock()
        )

        Assert.assertEquals(testCase.expected, underTest.targetItem(1))
    }

    @Test
    fun `item after next`() {
        val underTest = OverflowItemStrategy.Ring(
            testCase.nearestItem,
            testCase.itemCount,
            scrollRange = 0,
            scrollOffset = 0,
            metrics = mock()
        )

        Assert.assertEquals(testCase.nextItem, underTest.targetItem(2))
    }

    @Test
    fun `previous item`() {
        val underTest = OverflowItemStrategy.Ring(
            testCase.nearestItem,
            testCase.itemCount,
            scrollRange = 0,
            scrollOffset = 0,
            metrics = mock()
        )

        Assert.assertEquals(testCase.expected, underTest.targetItem(-1))
    }

    @Test
    fun `item before previous`() {
        val underTest = OverflowItemStrategy.Ring(
            testCase.nearestItem,
            testCase.itemCount,
            scrollRange = 0,
            scrollOffset = 0,
            metrics = mock()
        )

        Assert.assertEquals(testCase.previousItem, underTest.targetItem(-2))
    }

    private val TestCase.expected get() = if (itemCount > 0) nearestItem.mod(itemCount) else -1
}
