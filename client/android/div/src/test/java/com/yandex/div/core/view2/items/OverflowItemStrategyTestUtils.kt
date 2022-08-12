package com.yandex.div.core.view2.items

internal data class TestCase(
    val currentItem: Int,
    val itemCount: Int,
    val nextItem: Int,
    val previousItem: Int
)

internal fun baseOverflowCases(itemCount: Int, vararg testCases: TestCase): List<TestCase> {
    val cases = mutableListOf<TestCase>()
    cases += TestCase(currentItem = -1, itemCount = 0, nextItem = -1, previousItem = -1)
    cases += TestCase(currentItem = 0, itemCount = 1, nextItem = 0, previousItem = 0)
    for (i in 1 until itemCount - 1) {
        cases += TestCase(currentItem = i, itemCount = itemCount, nextItem = i + 1, previousItem = i - 1)
    }
    cases.addAll(testCases)
    return cases
}
