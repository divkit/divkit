package com.yandex.div.core.expression.local

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivPathUtils.getItemIds
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TabsStateCache
import com.yandex.div.core.state.TemporaryDivStateCache
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.build
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.state.DivStateCache
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import javax.inject.Inject

@DivScope
@Mockable
internal class DivRuntimeVisitor @Inject constructor(
    private val divStateCache: DivStateCache,
    private val temporaryStateCache: TemporaryDivStateCache,
    private val tabsCache: TabsStateCache,
) {

    fun createAndAttachRuntimes(
        rootDiv: Div,
        rootPath: DivStatePath,
        divView: Div2View,
    ) {
        val rootRuntime = divView.runtimeStore.rootRuntime
        rootRuntime.onAttachedToWindow(divView)
        visit(rootDiv, divView, rootPath, rootRuntime)
    }

    fun createAndAttachRuntimesToState(
        divView: Div2View,
        div: DivState,
        path: DivStatePath,
        expressionResolver: ExpressionResolver,
    ) {
        val runtime = divView.runtimeStore.getRuntimeWithOrNull(expressionResolver) ?: return
        visitStates(div, divView, path, runtime)
    }

    fun createAndAttachRuntimesToTabs(
        divView: Div2View,
        div: DivTabs,
        path: DivStatePath,
        expressionResolver: ExpressionResolver,
    ) {
        val runtime = divView.runtimeStore.getRuntimeWithOrNull(expressionResolver) ?: return
        visitTabs(div, divView, path, runtime)
    }

    private fun visit(
        div: Div,
        divView: Div2View,
        path: DivStatePath,
        parentRuntime: ExpressionsRuntime,
    ) {
        when (div) {
            is Div.Container ->
                visitContainer(div, divView, div.value.items, div.value.itemBuilder, path, parentRuntime)
            is Div.Grid -> visitContainer(div, divView, div.value.items, null, path, parentRuntime)
            is Div.Gallery ->
                visitContainer(div, divView, div.value.items, div.value.itemBuilder, path, parentRuntime)
            is Div.Pager ->
                visitContainer(div, divView, div.value.items, div.value.itemBuilder, path, parentRuntime)

            is Div.State -> visitState(div, divView, path, parentRuntime)
            is Div.Tabs -> visitTabs(div, divView, path, parentRuntime)

            is Div.Custom -> defaultVisit(div, divView, path, parentRuntime)
            is Div.GifImage -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Image -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Indicator -> defaultVisit(div, divView, path,parentRuntime)
            is Div.Input -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Select -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Separator -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Slider -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Text -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Video -> defaultVisit(div, divView, path, parentRuntime)
            is Div.Switch -> defaultVisit(div, divView, path, parentRuntime)
        }
    }

    private fun defaultVisit(
        div: Div,
        divView: Div2View,
        path: DivStatePath,
        parentRuntime: ExpressionsRuntime
    ): ExpressionsRuntime {
        return divView.runtimeStore.getOrCreateRuntime(path, div, parentRuntime.expressionResolver, divView).also {
            it.onAttachedToWindow(divView)
        }
    }

    private fun visitContainer(
        div: Div,
        divView: Div2View,
        items: List<Div>?,
        itemBuilder: DivCollectionItemBuilder?,
        path: DivStatePath,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)

        itemBuilder?.let {
            it.visit(divView, path, runtime)
            return
        }

        val ids = items?.getIds() ?: return
        items.forEachIndexed { index, item ->
            visit(item, divView, path.appendDiv(ids[index]), runtime)
        }
    }

    private fun DivCollectionItemBuilder.visit(
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        val builtItems = build(runtime.expressionResolver)
        val ids = builtItems.getItemIds()
        builtItems.forEachIndexed { index, item ->
            val childPath = path.appendDiv(ids[index])
            val childRuntime = divView.runtimeStore.resolveRuntimeWith(
                divView,
                childPath,
                item.div,
                item.expressionResolver,
                runtime.expressionResolver
            )
            visit(item.div, divView, childPath, childRuntime ?: runtime)
        }
    }

    private fun getActiveStateId(
        div: DivState,
        divView: Div2View,
        path: DivStatePath,
        resolver: ExpressionResolverImpl,
    ): String? {
        val statePath = "${path.statesString}/${path.lastDivId}"
        val cardId = divView.divTag.id

        return temporaryStateCache.getState(cardId, statePath)
            ?: divStateCache.getState(cardId, statePath)
            ?: div.stateIdVariable?.let {
                resolver.variableController.getMutableVariable(it)?.getValue().toString()
            }
            ?: div.defaultStateId?.evaluate(resolver)
            ?: div.states.firstOrNull()?.stateId
    }

    private fun visitState(
        div: Div.State,
        divView: Div2View,
        path: DivStatePath,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)
        visitStates(div.value, divView, path, runtime)
    }

    private fun visitStates(
        div: DivState,
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        val activeStateId = getActiveStateId(div, divView, path, runtime.expressionResolver)
        div.states.forEach {
            val childDiv = it.div ?: return@forEach
            val childPath = path.append(path.lastDivId, it, it.stateId)
            visitChild(childDiv, divView, childPath, runtime, it.stateId == activeStateId)
        }
    }

    private fun visitTabs(
        div: Div.Tabs,
        divView: Div2View,
        path: DivStatePath,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)
        visitTabs(div.value, divView, path, runtime)
    }

    private fun visitTabs(
        div: DivTabs,
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        val activeTab = tabsCache.getSelectedTab(divView.dataTag.id, path.fullPath)
            ?: div.selectedTab.evaluate(runtime.expressionResolver).toIntSafely()

        val ids = div.items.getIds({ this.div })
        div.items.forEachIndexed { index, tab ->
            visitChild(tab.div, divView, path.appendDiv(ids[index]), runtime, activeTab == index)
        }
    }

    private fun visitChild(
        div: Div,
        divView: Div2View,
        path: DivStatePath,
        parentRuntime: ExpressionsRuntime,
        isActive: Boolean,
    ) {
        if (isActive) {
            visit(div, divView, path, parentRuntime)
            return
        }

        val runtime = divView.runtimeStore.getOrCreateRuntime(path, div, parentRuntime.expressionResolver, divView)
        divView.runtimeStore.traverseFrom(runtime, path) {
            it.clearBinding(divView)
        }
    }
}
