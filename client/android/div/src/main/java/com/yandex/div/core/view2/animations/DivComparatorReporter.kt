package com.yandex.div.core.view2.animations

internal interface DivComparatorReporter {
    fun onComparisonSuccess() = Unit

    fun onComparisonNoOldData() = Unit

    fun onComparisonNoState() = Unit

    fun onComparisonDifferentClasses() = Unit

    fun onComparisonDifferentIdsWithTransition() = Unit

    fun onComparisonDifferentCustomTypes() = Unit

    fun onComparisonDifferentOverlap() = Unit

    fun onComparisonDifferentWrap() = Unit

    fun onComparisonDifferentChildCount() = Unit
}
