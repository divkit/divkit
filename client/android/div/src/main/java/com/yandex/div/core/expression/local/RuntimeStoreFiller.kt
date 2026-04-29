package com.yandex.div.core.expression.local

import com.yandex.div.DivDataTag
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivPathUtils.getItemIds
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.internal.core.build
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivData

internal class RuntimeStoreFiller(
    private val runtimeProvider: ExpressionsRuntimeProvider,
    private val errorCollector: ErrorCollector,
) {

    fun fillStore(store: RuntimeStoreImpl, data: DivData, tag: DivDataTag): ExpressionsRuntime {
        val runtime = runtimeProvider.createRootRuntime(data, tag.id, errorCollector, store)
        store.putRuntime(runtime, "", null)
        data.states.forEach { state ->
            val path = DivStatePath.fromState(state)
            val stateResolver = runtime.expressionResolver.let {
                if (state.div.hasLocalData) it else it.copy(path.fullPath)
            }
            visit(state.div, path, store, runtime, stateResolver)
        }
        return runtime
    }

    private fun visit(
        div: Div,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl? = null,
    ) {
        when (div) {
            is Div.Container -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Gallery -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Pager -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Grid -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Tabs -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.State -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Custom -> visit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Text -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Image -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.GifImage -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Separator -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Indicator -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Slider -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Input -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Select -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Video -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
            is Div.Switch -> defaultVisit(div, path, store, parentRuntime, intermediateResolver)
        }
    }

    private fun defaultVisit(
        div: Div,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ): ExpressionsRuntime {
        val runtime = if (div.hasLocalData) {
            val parentResolver = intermediateResolver ?: parentRuntime.expressionResolver
            runtimeProvider.createChildRuntime(path.fullPath, div, parentResolver, errorCollector)
        } else {
            val resolver = intermediateResolver ?: parentRuntime.expressionResolver.copyToChild(path.lastDivId)
            runtimeProvider.createRuntimeWithResolver(div, resolver, errorCollector)
        }

        store.putRuntime(runtime, path.fullPath, parentRuntime)
        return runtime
    }

    private val Div.hasLocalData get() = !value().variables.isNullOrEmpty() || !value().functions.isNullOrEmpty()

    private fun visitCollection(
        div: Div,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
        items: List<Div>?,
        builder: DivCollectionItemBuilder? = null,
        pathOverride: List<DivStatePath>? = null
    ) {
        val runtime = defaultVisit(div, path, store, parentRuntime, intermediateResolver)

        builder?.let {
            it.visit(path, store, runtime)
            return
        }

        val ids = items?.getIds() ?: return
        items.forEachIndexed { index, item ->
            val childPath = pathOverride?.get(index) ?: path.appendDiv(ids[index])
            visit(item, childPath, store, runtime)
        }
    }

    private fun DivCollectionItemBuilder.visit(
        path: DivStatePath,
        store: RuntimeStoreImpl,
        runtime: ExpressionsRuntime,
    ) {
        val builtItems = build(runtime.expressionResolver)
        val ids = builtItems.getItemIds()
        builtItems.forEachIndexed { index, item ->
            visit(item.div, path.appendDiv(ids[index]), store, runtime, item.expressionResolver.asImpl)
        }
    }

    private fun visit(
        div: Div.Container,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) = visitCollection(div, path, store, parentRuntime, intermediateResolver, div.value.items, div.value.itemBuilder)

    private fun visit(
        div: Div.Gallery,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) = visitCollection(div, path, store, parentRuntime, intermediateResolver, div.value.items, div.value.itemBuilder)

    private fun visit(
        div: Div.Pager,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) = visitCollection(div, path, store, parentRuntime, intermediateResolver, div.value.items, div.value.itemBuilder)

    private fun visit(
        div: Div.Grid,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) = visitCollection(div, path, store, parentRuntime, intermediateResolver, div.value.items)

    private fun visit(
        div: Div.Tabs,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) = visitCollection(div, path, store, parentRuntime, intermediateResolver, div.value.items.map { it.div })

    private fun visit(
        div: Div.State,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) {
        val id = div.value.getId()
        val paths = div.value.states.mapNotNull {
            it.div ?: return@mapNotNull null
            path.append(id, it, it.stateId)
        }
        val items = div.value.states.mapNotNull { it.div }
        visitCollection(div, path, store, parentRuntime, intermediateResolver, items, null, paths)
    }

    private fun visit(
        div: Div.Custom,
        path: DivStatePath,
        store: RuntimeStoreImpl,
        parentRuntime: ExpressionsRuntime,
        intermediateResolver: ExpressionResolverImpl?,
    ) = visitCollection(div, path, store, parentRuntime, intermediateResolver, div.value.items)
}
