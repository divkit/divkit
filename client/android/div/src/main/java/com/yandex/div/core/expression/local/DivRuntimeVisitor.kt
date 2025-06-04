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
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.state.DivStateCache
import com.yandex.div2.Div
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
        runtime.onAttachedToWindow(divView)

        visitStates(
            div = div,
            divView = divView,
            path = path.fullPath,
            states = path.getStatesFlat(),
            parentRuntime = runtime,
        )
    }

    fun createAndAttachRuntimesToTabs(
        divView: Div2View,
        div: DivTabs,
        path: DivStatePath,
        expressionResolver: ExpressionResolver,
    ) {
        val runtime = divView.runtimeStore?.getRuntimeWithOrNull(expressionResolver) ?: return
        runtime.onAttachedToWindow(divView)

        visitTabs(
            div = div,
            divView = divView,
            path = path.fullPath,
            states = path.getStatesFlat(),
            parentRuntime = runtime
        )
    }

    private fun visit(
        div: Div,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        when (div) {
            is Div.Container -> visitContainer(div, divView, div.value.items, path, states, parentRuntime)
            is Div.Grid -> visitContainer(div, divView, div.value.items, path, states, parentRuntime)
            is Div.Gallery -> visitContainer(div, divView, div.value.items, path, states, parentRuntime)
            is Div.Pager -> visitContainer(div, divView, div.value.items, path, states, parentRuntime)

            is Div.State -> visitStates(div.value, divView, path, states, parentRuntime)
            is Div.Tabs -> visitTabs(div.value, divView, path, states, parentRuntime)

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
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        val runtime = defaultVisit(div, divView, path, parentRuntime)

        items?.forEachIndexed { index, item ->
            visit(item, divView, path.appendChild(item.value().getChildPathUnit(index)), states, runtime)
        }
    }

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

    private fun visitStates(
        div: DivState,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        states.add(div.getId())
        val activeStateId = getActiveStateId(div, divView, states, parentRuntime.expressionResolver)
        div.states.forEach {
            val childDiv = it.div ?: return@forEach
            val childPath = path.appendChild(it.stateId)
            if (it.stateId == activeStateId) {
                visit(childDiv, divView, childPath, states, parentRuntime)
            } else {
                divView.runtimeStore?.tree?.invokeRecursively(parentRuntime, childPath) {
                    node -> node.runtime.clearBinding(divView)
                }
            }
        }
        states.removeLastOrNull()
    }

    private fun visitTabs(
        div: DivTabs,
        divView: Div2View,
        path: String,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ): ExpressionsRuntime? {
        val activeTab = tabsCache.getSelectedTab(divView.dataTag.id, path)
            ?: div.selectedTab.evaluate(parentRuntime.expressionResolver).toIntSafely()

        div.items.forEachIndexed { index, tab ->
            val childPath = path.appendChild(tab.div.value().getChildPathUnit(index))
            if (activeTab == index) {
                visit(tab.div, divView, childPath, states, parentRuntime)
            } else {
                divView.runtimeStore?.tree?.invokeRecursively(parentRuntime, childPath) {
                    node -> node.runtime.clearBinding(divView)
                }
            }
        }

        return null
    }

    private fun String.appendChild(id: String) = "$this/$id"
}

