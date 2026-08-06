package com.yandex.div.core.view2.animations

import com.yandex.div.core.util.isWrapContainer
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.itemsToDivBlocks
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom

internal object DivComparator {

    fun isDivDataReplaceable(
        oldBlock: DivBlock?,
        newBlock: DivBlock?,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (oldBlock == null) {
            reporter?.onComparisonNoOldData()
            return false
        }
        if (newBlock == null) {
            reporter?.onComparisonNoState()
            return false
        }
        return areDivsReplaceable(oldBlock, newBlock, reporter).also {
            if (it) reporter?.onComparisonSuccess()
        }
    }

    fun areDivsReplaceable(
        old: DivBlock?,
        new: DivBlock?,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (old?.div?.javaClass != new?.div?.javaClass) {
            reporter?.onComparisonDifferentClasses()
            return false
        }
        if (old == null || new == null || old === new) {
            return true
        }
        return areValuesReplaceable(old, new, reporter) &&
            areChildrenReplaceable(extractChildren(old), extractChildren(new), reporter)
    }

    fun areValuesReplaceable(
        oldBlock: DivBlock,
        newBlock: DivBlock,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        val old = oldBlock.div.value()
        val new = newBlock.div.value()

        if (old.id != null && new.id != null && old.id != new.id && (old.hasTransitions() || new.hasTransitions())) {
            reporter?.onComparisonDifferentIdsWithTransition()
            return false
        }
        if (old is DivCustom && new is DivCustom && old.customType != new.customType) {
            reporter?.onComparisonDifferentCustomTypes()
            return false
        }
        if (old is DivContainer && new is DivContainer) {
            if (old.isOverlap(oldBlock.expressionResolver) != new.isOverlap(newBlock.expressionResolver)) {
                reporter?.onComparisonDifferentOverlap()
                return false
            }
            if (old.isWrapContainer(oldBlock.expressionResolver) != new.isWrapContainer(newBlock.expressionResolver)) {
                reporter?.onComparisonDifferentWrap()
                return false
            }
        }
        return true
    }

    fun areChildrenReplaceable(
        oldChildren: List<DivBlock>,
        newChildren: List<DivBlock>,
        reporter: DivComparatorReporter? = null,
    ): Boolean {
        if (oldChildren.size != newChildren.size) {
            reporter?.onComparisonDifferentChildCount()
            return false
        }

        return oldChildren.zip(newChildren).all {
            areDivsReplaceable(it.first, it.second, reporter)
        }
    }

    private fun extractChildren(divBlock: DivBlock): List<DivBlock> {
        return when (divBlock) {
            is DivBlock.Container -> divBlock.buildItems()
            is DivBlock.Grid -> divBlock.itemsToDivBlocks()
            is DivBlock.Image -> emptyList()
            is DivBlock.GifImage -> emptyList()
            is DivBlock.Text -> emptyList()
            is DivBlock.Separator -> emptyList()
            is DivBlock.Gallery -> emptyList()
            is DivBlock.Pager -> emptyList()
            is DivBlock.Tabs -> emptyList()
            is DivBlock.State -> emptyList()
            is DivBlock.Custom -> emptyList()
            is DivBlock.Input -> emptyList()
            is DivBlock.Select -> emptyList()
            is DivBlock.Indicator -> emptyList()
            is DivBlock.Slider -> emptyList()
            is DivBlock.Video -> emptyList()
            is DivBlock.Switch -> emptyList()
        }
    }

    private fun DivBase.hasTransitions(): Boolean {
        return transitionIn != null || transitionOut != null || transitionChange != null
    }

    private fun DivContainer.isOverlap(resolver: ExpressionResolver) =
        orientation.evaluate(resolver) == DivContainer.Orientation.OVERLAP
}
