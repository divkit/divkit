package com.yandex.div.core.expression.local

import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivPathUtils.append
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivPathUtils.statePath
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TabsStateCache
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.build
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import javax.inject.Inject

@DivDataScope
internal class DivRuntimeVisitor @Inject constructor(
    private val stateManager: DivStateManager,
    private val tabsCache: TabsStateCache,
) {

    fun createAndAttachRuntimes(
        rootDiv: Div,
        rootPath: DivStatePath,
        divView: Div2View,
    ) {
        val rootRuntime = divView.runtimeStore.rootRuntime
        rootRuntime.onAttachedToWindow(divView)
        val rootDivRuntime = divView.runtimeStore
            .getOrCreateRuntime(rootPath.fullPath, rootDiv, rootRuntime.expressionResolver)
        visit(rootDiv, divView, rootPath, rootDivRuntime)
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
        runtime: ExpressionsRuntime,
    ) {
        when (div) {
            is Div.Container -> visitContainer(divView, div.value.items, div.value.itemBuilder, path, runtime)
            is Div.Grid -> visitContainer(divView, div.value.items, null, path, runtime)
            is Div.Gallery -> visitContainer(divView, div.value.items, div.value.itemBuilder, path, runtime)
            is Div.Pager -> visitContainer(divView, div.value.items, div.value.itemBuilder, path, runtime)

            is Div.State -> visitState(div, divView, path, runtime)
            is Div.Tabs -> visitTabs(div, divView, path, runtime)

            is Div.Custom -> defaultVisit(runtime, divView)
            is Div.GifImage -> defaultVisit(runtime, divView)
            is Div.Image -> defaultVisit(runtime, divView)
            is Div.Indicator -> defaultVisit(runtime, divView)
            is Div.Input -> defaultVisit(runtime, divView)
            is Div.Select -> defaultVisit(runtime, divView)
            is Div.Separator -> defaultVisit(runtime, divView)
            is Div.Slider -> defaultVisit(runtime, divView)
            is Div.Text -> defaultVisit(runtime, divView)
            is Div.Video -> defaultVisit(runtime, divView)
            is Div.Switch -> defaultVisit(runtime, divView)
        }
    }

    private fun defaultVisit(runtime: ExpressionsRuntime, divView: Div2View) = runtime.onAttachedToWindow(divView)

    private fun visitContainer(
        divView: Div2View,
        items: List<Div>?,
        itemBuilder: DivCollectionItemBuilder?,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        defaultVisit(runtime, divView)

        itemBuilder?.let {
            it.visit(divView, path, runtime)
            return
        }

        val ids = items?.getIds() ?: return
        items.forEachIndexed { index, item ->
            val childPath = path.appendDiv(ids[index])
            val childRuntime = divView.runtimeStore
                .getOrCreateRuntime(childPath.fullPath, item, runtime.expressionResolver)
            visit(item, divView, childPath, childRuntime)
        }
    }

    private fun DivCollectionItemBuilder.visit(
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        build(runtime.expressionResolver, path).forEach {
            val runtime = divView.runtimeStore.getRuntimeWithOrNull(it.expressionResolver) ?: runtime
            visit(it.div, divView, it.path, runtime)
        }
    }

    private fun visitState(
        div: Div.State,
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        defaultVisit(runtime, divView)
        visitStates(div.value, divView, path, runtime)
    }

    private fun visitStates(
        div: DivState,
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        val activeStateId = stateManager.getState(div, runtime.expressionResolver, path.statePath)
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
        runtime: ExpressionsRuntime,
    ) {
        defaultVisit(runtime, divView)
        visitTabs(div.value, divView, path, runtime)
    }

    private fun visitTabs(
        div: DivTabs,
        divView: Div2View,
        path: DivStatePath,
        runtime: ExpressionsRuntime,
    ) {
        val activeTab = tabsCache.getSelectedTab(path.fullPath)
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
        val runtime = divView.runtimeStore.getOrCreateRuntime(path.fullPath, div, parentRuntime.expressionResolver)
        if (isActive) {
            visit(div, divView, path, runtime)
            return
        }

        divView.runtimeStore.traverseFrom(runtime, path) {
            it.clearBinding(divView)
        }
    }
}
