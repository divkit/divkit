package com.yandex.div.core.view2.items

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class ClampOverflowItemStrategyTest(private val testCase: TestCase) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<TestCase> {
            val itemCount = 5
            val edgeCase1 = TestCase(currentItem = 0, itemCount = itemCount, nextItem = 1, previousItem = 0)
            val edgeCase2 = TestCase(
                currentItem = itemCount - 1,
                itemCount = itemCount,
                nextItem = itemCount - 1,
                previousItem = itemCount - 2
            )
            return baseOverflowCases(itemCount, edgeCase1, edgeCase2)
        }
    }

    @Test
    fun `next item`() {
        val underTest = OverflowItemStrategy.Clamp(currentItem = testCase.currentItem, itemCount = testCase.itemCount)

        Assert.assertEquals(testCase.nextItem, underTest.nextItem())
    }


    @Test
    fun `previous item`() {
        val underTest = OverflowItemStrategy.Clamp(currentItem = testCase.currentItem, itemCount = testCase.itemCount)

        Assert.assertEquals(testCase.previousItem, underTest.previousItem())
    }
}
