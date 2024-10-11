package com.yandex.div.core.expression.local

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TabsStateCache
import com.yandex.div.core.state.TemporaryDivStateCache
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.util.toVariables
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.getChildPathUnit
import com.yandex.div.internal.Assert
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
            path = rootPath.path.toMutableList(),
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
            path = path.path.toMutableList(),
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
            path = path.path.toMutableList(),
            states = path.getStatesFlat(),
            parentRuntime = runtime
        )
    }

    private fun visit(
        div: Div,
        divView: Div2View,
        path: MutableList<String>,
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
        }
    }

    private fun defaultVisit(
        div: Div,
        divView: Div2View,
        path: MutableList<String>,
        parentRuntime: ExpressionsRuntime?
    ): ExpressionsRuntime? {
        return if (div.value().variables.isNullOrEmpty() && div.value().variableTriggers.isNullOrEmpty()) {
            parentRuntime
        } else {
            val stringPath = path.joinToString("/")
            divView.runtimeStore?.getOrCreateRuntime(
                path = stringPath,
                variables = div.value().variables?.toVariables(),
                triggers = div.value().variableTriggers,
                parentRuntime = parentRuntime,
            )?.also { runtime -> runtime.onAttachedToWindow(divView) } ?: run {
                Assert.fail("ExpressionRuntimeVisitor cannot create runtime for path = $stringPath")
                null
            }
        }
    }

    private fun visitContainer(
        div: Div,
        divView: Div2View,
        items: List<Div>?,
        path: MutableList<String>,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        defaultVisit(div, divView, path, parentRuntime).also { runtime ->
            if (runtime == null) return@also

            items?.forEachIndexed { index, div ->
                if (div !is Div.State) {
                    path.add(div.value().getChildPathUnit(index))
                    visit(div, divView, path, states, runtime)
                    path.removeLastOrNull()
                } else {
                    visit(div, divView, path, states, runtime)
                }
            }
        }
    }

    private fun getActiveStateId(
        div: DivState,
        divView: Div2View,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ): String? {
        val statePath = states.joinToString("/")
        val cardId = divView.divTag.id

        return temporaryStateCache.getState(cardId, statePath)
            ?: divStateCache.getState(cardId, statePath)
            ?: div.stateIdVariable?.let {
                parentRuntime.variableController.getMutableVariable(it)?.getValue().toString()
            }
            ?: parentRuntime.let { div.defaultStateId?.evaluate(it.expressionResolver) }
            ?: div.states.firstOrNull()?.stateId
    }

    private fun visitStates(
        div: DivState,
        divView: Div2View,
        path: MutableList<String>,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ) {
        val id = div.id ?: div.divId ?: return
        path.add(id)
        states.add(id)

        val activeStateId = getActiveStateId(div, divView, states, parentRuntime)
        div.states.forEach {
            it.div?.let { childDiv ->
                path.add(it.stateId)

                if (it.stateId == activeStateId) {
                    visit(childDiv, divView, path, states, parentRuntime)
                } else {
                    divView.runtimeStore?.tree?.invokeRecursively(
                        parentRuntime, path.joinToString("/")
                    ) { node -> node.runtime.clearBinding() }
                }

                path.removeLastOrNull()
            }
        }
        path.removeLastOrNull()
        states.removeLastOrNull()
    }

    private fun visitTabs(
        div: DivTabs,
        divView: Div2View,
        path: MutableList<String>,
        states: MutableList<String>,
        parentRuntime: ExpressionsRuntime,
    ): ExpressionsRuntime? {
        val activeTab = tabsCache.getSelectedTab(divView.dataTag.id, path.joinToString("/"))
            ?: div.selectedTab.evaluate(parentRuntime.expressionResolver).toIntSafely()

        div.items.forEachIndexed { index, tab ->
            val id = tab.div.value().getChildPathUnit(index)
            path.add(id)

            if (activeTab == index) {
                visit(tab.div, divView, path, states, parentRuntime)
            } else {
                divView.runtimeStore?.tree?.invokeRecursively(
                    parentRuntime, path.joinToString("/")
                ) { node -> node.runtime.clearBinding() }
            }

            path.removeLastOrNull()
        }

        return null
    }
}

