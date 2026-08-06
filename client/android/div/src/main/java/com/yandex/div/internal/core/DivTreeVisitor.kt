package com.yandex.div.internal.core

import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData

internal abstract class DivTreeVisitor<T>(private val returnCondition: ((T) -> Boolean)? = null) {

    protected fun visit(data: DivData, resolver: ExpressionResolver) {
        data.states.forEach { state ->
            val path = DivStatePath.fromState(state)
            val stateResolver = resolver.asImpl?.runtimeStore
                ?.getOrCreateRuntime(path.fullPath, state.div, resolver)?.expressionResolver
                ?: resolver
            visit(DivBlock.create(state.div, stateResolver, path))
        }
    }

    fun visit(divBlock: DivBlock): T {
        return when (divBlock) {
            is DivBlock.Text -> visitText(divBlock)
            is DivBlock.Image -> visitImage(divBlock)
            is DivBlock.GifImage -> visitGifImage(divBlock)
            is DivBlock.Separator -> visitSeparator(divBlock)
            is DivBlock.Container -> visitContainer(divBlock)
            is DivBlock.Grid -> visitGrid(divBlock)
            is DivBlock.Gallery -> visitGallery(divBlock)
            is DivBlock.Pager -> visitPager(divBlock)
            is DivBlock.Tabs -> visitTabs(divBlock)
            is DivBlock.State -> visitState(divBlock)
            is DivBlock.Custom -> visitCustom(divBlock)
            is DivBlock.Indicator -> visitIndicator(divBlock)
            is DivBlock.Slider -> visitSlider(divBlock)
            is DivBlock.Input -> visitInput(divBlock)
            is DivBlock.Select -> visitSelect(divBlock)
            is DivBlock.Video -> visitVideo(divBlock)
            is DivBlock.Switch -> visitSwitch(divBlock)
        }
    }

    protected abstract fun defaultVisit(divBlock: DivBlock): T

    protected open fun defaultVisitCollection(divBlock: DivBlock, items: List<DivBlock>?): T {
        val result = defaultVisit(divBlock)
        if (returnCondition?.invoke(result) == true) return result

        items?.forEach {
            val child = visitCollectionChild(it, result)
            if (returnCondition?.invoke(child) == true) return child
        }
        return result
    }

    protected open fun visitCollectionChild(block: DivBlock, parent: T) = visit(block)

    protected open fun visitContainer(block: DivBlock.Container) = defaultVisitCollection(block, block.buildItems())

    protected open fun visitGrid(block: DivBlock.Grid) = defaultVisitCollection(block, block.itemsToDivBlocks())

    protected open fun visitGallery(block: DivBlock.Gallery) = defaultVisitCollection(block, block.buildItems())

    protected open fun visitPager(block: DivBlock.Pager) = defaultVisitCollection(block, block.buildItems())

    protected open fun visitTabs(block: DivBlock.Tabs) = defaultVisitCollection(block, block.itemsToDivBlocks())

    protected open fun visitState(block: DivBlock.State) = defaultVisitCollection(block, block.statesToDivBlocks())

    protected open fun visitCustom(block: DivBlock.Custom) = defaultVisitCollection(block, block.itemsToDivBlocks())

    protected open fun visitText(block: DivBlock.Text) = defaultVisit(block)

    protected open fun visitImage(block: DivBlock.Image) = defaultVisit(block)

    protected open fun visitGifImage(block: DivBlock.GifImage) = defaultVisit(block)

    protected open fun visitSeparator(block: DivBlock.Separator) = defaultVisit(block)

    protected open fun visitIndicator(block: DivBlock.Indicator) = defaultVisit(block)

    protected open fun visitSlider(block: DivBlock.Slider) = defaultVisit(block)

    protected open fun visitInput(block: DivBlock.Input) = defaultVisit(block)

    protected open fun visitSelect(block: DivBlock.Select) = defaultVisit(block)

    protected open fun visitVideo(block: DivBlock.Video) = defaultVisit(block)

    protected open fun visitSwitch(block: DivBlock.Switch) = defaultVisit(block)
}
