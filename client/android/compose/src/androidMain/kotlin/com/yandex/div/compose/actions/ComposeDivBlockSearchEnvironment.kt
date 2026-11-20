package com.yandex.div.compose.actions

import com.yandex.div.compose.collection.CollectionItemScopeStorage
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.declaresLocalScope
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.state.resolveActiveState
import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div.compose.utils.children
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.DivBlockSearchEnvironment
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData

/** Supplies Compose scopes and state paths to the model target locator. */
internal class ComposeDivBlockSearchEnvironment(
    private val state: DivData.State?,
    private val rootComponent: Lazy<DivLocalComponent>,
    private val localComponents: DivLocalComponentStorage,
    private val collectionItems: CollectionItemScopeStorage,
    private val stateStorage: DivStateStorage,
    private val tooltipStateStorage: TooltipStateStorage,
) : DivBlockSearchEnvironment {
    private val components = mutableMapOf<DivBlock, DivLocalComponent>()
    private val resolverPaths = mutableMapOf<ExpressionResolver, MutableList<DivStatePath>>()
    private val activePaths = mutableSetOf<DivStatePath>()

    override fun rootBlock(): DivBlock? {
        val currentState = state ?: return null
        val root = currentState.div
        val path = DivStatePath.fromState(currentState.stateId)
        return createBlock(root, rootComponent.value, path)
    }

    override fun findDescendants(root: DivBlock): Iterable<DivBlock> {
        val component = components[root] ?: return emptyList()
        val blocks = mutableListOf<DivBlock>()
        visit(root, component, blocks)
        return blocks
    }

    override fun activeStatesPaths(): Set<DivStatePath> = activePaths

    override fun findPathsFor(resolver: ExpressionResolver): Collection<DivStatePath> =
        resolverPaths[resolver].orEmpty()

    override fun itemBuilderSegment(resolver: ExpressionResolver): String? = null

    private fun visit(
        block: DivBlock,
        component: DivLocalComponent,
        blocks: MutableList<DivBlock>,
        tooltipAnchorStateCount: Int = 0,
    ) {
        blocks.add(block)
        val div = block.div
        visitShownTooltips(block, component, blocks)
        if (div is Div.State) {
            visitActiveState(div, block.path, component, blocks, tooltipAnchorStateCount)
        } else if (div.hasItemBuilder()) {
            visitBuiltItems(div, block.path, component, blocks, tooltipAnchorStateCount)
        } else {
            div.children().forEachIndexed { index, child ->
                val childPath = block.path.appendDiv(child.pathSegment(index))
                visitChild(child, childPath, component, blocks, tooltipAnchorStateCount)
            }
        }
    }

    private fun visitShownTooltips(
        block: DivBlock,
        component: DivLocalComponent,
        blocks: MutableList<DivBlock>,
    ) {
        val tooltips = block.div.value().tooltips ?: return
        for (tooltip in tooltips) {
            val anchor = tooltipStateStorage.getAnchor(tooltip.id)
            val isAnchor = anchor != null && anchor.tooltip === tooltip &&
                anchor.localComponent === component
            if (!isAnchor || !tooltipStateStorage.isShown(tooltip.id)) {
                continue
            }
            val path = block.path.appendDiv("tooltip:${tooltip.id}")
            val anchorStateCount = block.path.getStates().size
            visitChild(tooltip.div, path.appendDiv("content"), component, blocks, anchorStateCount)
            tooltip.substrateDiv?.let {
                visitChild(it, path.appendDiv("substrate"), component, blocks, anchorStateCount)
            }
        }
    }

    private fun visitBuiltItems(
        div: Div,
        path: DivStatePath,
        component: DivLocalComponent,
        blocks: MutableList<DivBlock>,
        tooltipAnchorStateCount: Int,
    ) {
        collectionItems.getItems(div.value(), component).forEachIndexed { index, item ->
            val segment = item.div.pathSegment(index)
            visitChild(
                div = item.div,
                path = path.appendDiv(segment),
                parent = item.component,
                blocks = blocks,
                tooltipAnchorStateCount = tooltipAnchorStateCount,
            )
        }
    }

    private fun visitActiveState(
        div: Div.State,
        path: DivStatePath,
        component: DivLocalComponent,
        blocks: MutableList<DivBlock>,
        tooltipAnchorStateCount: Int,
    ) {
        val state = div.value
        // Popup content has the card's root state path in composition. Scope lookup
        // also keeps the anchor's path so that nearby targets in repeated items are preferred.
        val pathInComposition = if (tooltipAnchorStateCount == 0) {
            path
        } else {
            DivStatePath(
                topLevelStateId = path.topLevelStateId,
                states = path.getStates().drop(tooltipAnchorStateCount),
            )
        }
        val selectedState =
            state.resolveActiveState(component.expressionResolver, stateStorage, pathInComposition)
                ?: return
        val child = selectedState.div ?: return
        val childPath = path.append(state.id.orEmpty(), selectedState.stateId, selectedState.stateId)
        activePaths.add(childPath)
        visitChild(child, childPath, component, blocks, tooltipAnchorStateCount)
    }

    private fun visitChild(
        div: Div,
        path: DivStatePath,
        parent: DivLocalComponent,
        blocks: MutableList<DivBlock>,
        tooltipAnchorStateCount: Int,
    ) {
        val block = createBlock(div, parent, path) ?: return
        visit(block, components.getValue(block), blocks, tooltipAnchorStateCount)
    }

    private fun createBlock(
        div: Div,
        parent: DivLocalComponent,
        path: DivStatePath,
    ): DivBlock? {
        val component = if (div.value().declaresLocalScope) {
            localComponents.get(div.value()) ?: return null
        } else {
            parent
        }
        val resolver = component.expressionResolver
        val block = DivBlock.create(div, resolver, path)
        components[block] = component
        resolverPaths.getOrPut(resolver) { mutableListOf() }.add(path)
        return block
    }
}

private fun Div.pathSegment(index: Int): String = "${value().id.orEmpty()}#$index"

private fun Div.hasItemBuilder(): Boolean = when (this) {
    is Div.Container -> value.itemBuilder != null
    is Div.Gallery -> value.itemBuilder != null
    is Div.Pager -> value.itemBuilder != null
    is Div.Custom, is Div.GifImage, is Div.Grid, is Div.Image, is Div.Indicator,
    is Div.Input, is Div.Select, is Div.Separator, is Div.Slider, is Div.State,
    is Div.Switch, is Div.Tabs, is Div.Text, is Div.Video -> false
}
