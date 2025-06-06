package com.yandex.div.core.expression.local

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TabsStateCache
import com.yandex.div.core.state.TemporaryDivStateCache
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.getChildPathUnit
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

    private fun DivStatePath.getStatesFlat() =
        ArrayList<String>(getStates().size * 4).apply {
            add(topLevelStateId.toString())
            getStates().forEach {
                add(it.first)
                add(it.second)
            }
        }

    fun createAndAttachRuntimes(
        rootDiv: Div,
        rootPath: DivStatePath,
        divView: Div2View,
    ) {
        val rootRuntime = divView.runtimeStore?.rootRuntime ?: return
        rootRuntime.onAttachedToWindow(divView)

        visit(
            div = rootDiv,
            divView = divView,
            path = rootPath.fullPath,
            states = rootPath.getStatesFlat(),
            parentRuntime = rootRuntime,
        )
    }

    fun createAndAttachRuntimesToState(
        divView: Div2View,
        div: DivState,
        path: DivStatePath,
        expressionResolver: ExpressionResolver,
    ) {
        val runtime = divView.runtimeStore?.getRuntimeWithOrNull(expressionResolver) ?: return
        visitStates(div, divView, path.fullPath, path.getStatesFlat(), runtime)
    }

    fun createAndAttachRuntimesToTabs(
        divView: Div2View,
        div: DivTabs,
        path: DivStatePath,
        expressionResolver: ExpressionResolver,
    ) {
        val runtime = divView.runtimeStore?.getRuntimeWithOrNull(expressionResolver) ?: return
        visitTabs(div, divView, path.fullPath, path.getStatesFlat(), runtime)
    }

    private fun visit(
        div: Div,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        when (div) {
            is Div.Container ->
                visitContainer(div, divView, div.value.items, div.value.itemBuilder, path, states, parentRuntime)
            is Div.Grid -> visitContainer(div, divView, div.value.items, null, path, states, parentRuntime)
            is Div.Gallery ->
                visitContainer(div, divView, div.value.items, div.value.itemBuilder, path, states, parentRuntime)
            is Div.Pager ->
                visitContainer(div, divView, div.value.items, div.value.itemBuilder, path, states, parentRuntime)

            is Div.State -> visitState(div, divView, path, states, parentRuntime)
            is Div.Tabs -> visitTabs(div, divView, path, states, parentRuntime)

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
        path: String,
        parentRuntime: ExpressionsRuntime
    ): ExpressionsRuntime {
        return parentRuntime.runtimeStore.getOrCreateRuntime(path, div, parentRuntime.expressionResolver).also {
            it.onAttachedToWindow(divView)
        }
    }

    private fun visitContainer(
        div: Div,
        divView: Div2View,
        items: List<Div>?,
        itemBuilder: DivCollectionItemBuilder?,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)

        itemBuilder?.let {
            it.visit(divView, path, states, runtime)
            return
        }

        items?.forEachIndexed { index, item ->
            visit(item, divView, path.appendChild(item, index), states, runtime)
        }
    }

    private fun DivCollectionItemBuilder.visit(
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        runtime: ExpressionsRuntime,
    ) {
        build(divView, runtime.expressionResolver).forEachIndexed { index, item ->
            val childPath = path.appendChild(item.div, index)
            val childRuntime = runtime.runtimeStore.resolveRuntimeWith(
                divView,
                childPath,
                item.div,
                item.expressionResolver,
                runtime.expressionResolver
            )
            visit(item.div, divView, childPath, states, childRuntime ?: runtime)
        }
    }

    private fun String.appendChild(div: Div, index: Int) = appendChild(div.value().getChildPathUnit(index))

    private fun getActiveStateId(
        div: DivState,
        divView: Div2View,
        states: MutableList<String>,
        resolver: ExpressionResolverImpl,
    ): String? {
        val statePath = states.joinToString("/")
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
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)
        visitStates(div.value, divView, path, states, runtime)
    }

    private fun visitStates(
        div: DivState,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        runtime: ExpressionsRuntime,
    ) {
        states.add(div.getId())
        val activeStateId = getActiveStateId(div, divView, states, runtime.expressionResolver)
        div.states.forEach {
            val childDiv = it.div ?: return@forEach
            val childPath = path.appendChild(it.stateId)
            visitChild(childDiv, divView, childPath, states, runtime, it.stateId == activeStateId)
        }
        states.removeLastOrNull()
    }

    private fun visitTabs(
        div: Div.Tabs,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)
        visitTabs(div.value, divView, path, states, runtime)
    }

    private fun visitTabs(
        div: DivTabs,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        runtime: ExpressionsRuntime,
    ) {
        val activeTab = tabsCache.getSelectedTab(divView.dataTag.id, path)
            ?: div.selectedTab.evaluate(runtime.expressionResolver).toIntSafely()

        div.items.forEachIndexed { index, tab ->
            visitChild(tab.div, divView, path.appendChild(tab.div, index), states, runtime, activeTab == index)
        }
    }

    private fun visitChild(
        div: Div,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
        isActive: Boolean,
    ) {
        if (isActive) {
            visit(div, divView, path, states, parentRuntime)
            return
        }

        val runtime = parentRuntime.runtimeStore.getOrCreateRuntime(path, div, parentRuntime.expressionResolver)
        runtime.runtimeStore.tree.invokeRecursively(runtime, path) { node ->
            node.runtime.clearBinding(divView)
        }
    }

    private fun String.appendChild(id: String) = "$this/$id"
}

