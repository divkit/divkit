package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.toVariables
import com.yandex.div.core.view2.divs.getChildPathUnit
import com.yandex.div.internal.Assert
import com.yandex.div2.Div

internal class DivRuntimeTree(
    private val rootDiv: Div,
    private val rootPath: DivStatePath,
    private val runtimeStore: RuntimeStore,
) {
    fun createRuntimes() {
        val rootRuntime = runtimeStore.rootRuntime ?: run {
            Assert.fail(ERROR_ROOT_RUNTIME_NOT_SPECIFIED)
            return
        }
        visit(rootDiv, rootPath.path.toMutableList(), rootRuntime)
    }

    private fun visit(
        div: Div,
        path: MutableList<String>,
        parentRuntime: ExpressionsRuntime?,
    ) = when (div) {
        is Div.Container -> visitContainer(div, div.value.items, path, parentRuntime)
        is Div.Grid -> visitContainer(div, div.value.items, path, parentRuntime)
        is Div.Gallery -> visitContainer(div, div.value.items, path, parentRuntime)
        is Div.Pager -> visitContainer(div, div.value.items, path, parentRuntime)
        is Div.State -> visitContainer(div, div.value.states.mapNotNull { it.div }, path, parentRuntime)
        is Div.Tabs -> visitContainer(div, div.value.items.map { it.div }, path, parentRuntime)

        is Div.Custom -> defaultVisit(div, path, parentRuntime)
        is Div.GifImage -> defaultVisit(div, path, parentRuntime)
        is Div.Image -> defaultVisit(div, path, parentRuntime)
        is Div.Indicator -> defaultVisit(div, path, parentRuntime)
        is Div.Input -> defaultVisit(div, path, parentRuntime)
        is Div.Select -> defaultVisit(div, path, parentRuntime)
        is Div.Separator -> defaultVisit(div, path, parentRuntime)
        is Div.Slider -> defaultVisit(div, path, parentRuntime)
        is Div.Text -> defaultVisit(div, path, parentRuntime)
        is Div.Video -> defaultVisit(div, path, parentRuntime)
    }

    private fun defaultVisit(
        div: Div,
        path: MutableList<String>,
        parentRuntime: ExpressionsRuntime?,
    ): ExpressionsRuntime? {
        return if (div.value().variables.isNullOrEmpty() && div.value().variableTriggers.isNullOrEmpty()) {
            parentRuntime
        } else {
            val stringPath = path.joinToString("/")
            runtimeStore.getOrCreateRuntime(
                path = stringPath,
                variables = div.value().variables?.toVariables(),
                triggers = div.value().variableTriggers,
                parentRuntime = parentRuntime,
            ) ?: run {
                Assert.fail("ExpressionRuntimeVisitor cannot create runtime for path = $stringPath")
                null
            }
        }
    }

    private fun visitContainer(
        div: Div,
        items: List<Div>?,
        path: MutableList<String>,
        parentRuntime: ExpressionsRuntime?,
    ): ExpressionsRuntime? {
        return defaultVisit(div, path, parentRuntime).also { runtime ->
            items?.forEachIndexed { index, div ->
                path.add(div.value().getChildPathUnit(index))
                visit(div, path, runtime)
                path.removeLastOrNull()
            }
        }
    }
}

