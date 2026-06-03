package com.yandex.div.internal.core

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.state.DivPathUtils.append
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivPathUtils.getItemIds
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivData

internal abstract class DivTreeVisitor<T>(private val returnCondition: ((T) -> Boolean)? = null) {

    protected fun visit(data: DivData, resolver: ExpressionResolver) {
        data.states.forEach { state ->
            val path = DivStatePath.fromState(state)
            val stateResolver = resolver.asImpl?.runtimeStore
                ?.getOrCreateRuntime(path.fullPath, state.div, resolver)?.expressionResolver
                ?: resolver
            visit(state.div, stateResolver, path)
        }
    }

    fun visit(div: Div, resolver: ExpressionResolver, path: DivStatePath): T {
        return when (div) {
            is Div.Text -> visit(div, resolver, path)
            is Div.Image -> visit(div, resolver, path)
            is Div.GifImage -> visit(div, resolver, path)
            is Div.Separator -> visit(div, resolver, path)
            is Div.Container -> visit(div, resolver, path)
            is Div.Grid -> visit(div, resolver, path)
            is Div.Gallery -> visit(div, resolver, path)
            is Div.Pager -> visit(div, resolver, path)
            is Div.Tabs -> visit(div, resolver, path)
            is Div.State -> visit(div, resolver, path)
            is Div.Custom -> visit(div, resolver, path)
            is Div.Indicator -> visit(div, resolver, path)
            is Div.Slider -> visit(div, resolver, path)
            is Div.Input -> visit(div, resolver, path)
            is Div.Select -> visit(div, resolver, path)
            is Div.Video -> visit(div, resolver, path)
            is Div.Switch -> visit(div, resolver, path)
        }
    }

    protected abstract fun defaultVisit(data: Div, resolver: ExpressionResolver, path: DivStatePath): T

    protected open fun defaultVisitCollection(
        data: Div,
        resolver: ExpressionResolver,
        path: DivStatePath,
        items: List<Div>?,
        itemBuilder: DivCollectionItemBuilder? = null,
        pathOverride: List<DivStatePath>? = null
    ): T {
        val result = defaultVisit(data, resolver, path)
        if (returnCondition?.invoke(result) == true) return result

        itemBuilder?.let {
            val resolverImpl = resolver.asImpl ?: return result
            return it.visit(resolverImpl, path, result)
        }

        val ids = items?.getIds() ?: return result
        items.forEachIndexed { index, div ->
            val childPath = pathOverride?.get(index) ?: path.appendDiv(ids[index])
            val child = visitCollectionChild(div, resolver, childPath, result)
            if (returnCondition?.invoke(child) == true) return child
        }
        return result
    }

    private fun DivCollectionItemBuilder.visit(
        resolver: ExpressionResolverImpl,
        path: DivStatePath,
        parent: T,
    ): T {
        val builtItems = build(resolver)
        val ids = builtItems.getItemIds()
        builtItems.forEachIndexed { index, item ->
            val childPath = path.appendDiv(ids[index])
            val childResolver = resolver.runtimeStore.resolveRuntimeWith(
                childPath,
                item.div,
                item.expressionResolver,
                resolver
            )?.expressionResolver ?: item.expressionResolver

            val result = visitCollectionChild(item.div, childResolver, childPath, parent)
            if (returnCondition?.invoke(result) == true) return result
        }
        return parent
    }

    protected open fun visitCollectionChild(div: Div, resolver: ExpressionResolver, path: DivStatePath, parent: T) =
        visit(div, resolver, path)

    protected open fun visit(data: Div.Container, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisitCollection(data, resolver, path, data.value.items, data.value.itemBuilder)

    protected open fun visit(data: Div.Grid, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisitCollection(data, resolver, path, data.value.items)

    protected open fun visit(data: Div.Gallery, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisitCollection(data, resolver, path, data.value.items, data.value.itemBuilder)

    protected open fun visit(data: Div.Pager, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisitCollection(data, resolver, path, data.value.items, data.value.itemBuilder)

    protected open fun visit(data: Div.Tabs, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisitCollection(data, resolver, path, data.value.items.map { it.div })

    protected open fun visit(data: Div.State, resolver: ExpressionResolver, path: DivStatePath): T {
        val id = data.value.getId()
        val paths = data.value.states.mapNotNull {
            it.div ?: return@mapNotNull null
            path.append(id, it, it.stateId)
        }
        return defaultVisitCollection(data, resolver, path, data.value.states.mapNotNull { it.div }, null, paths)
    }

    protected open fun visit(data: Div.Custom, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisitCollection(data, resolver, path, data.value.items)

    protected open fun visit(data: Div.Text, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Image, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.GifImage, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Separator, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Indicator, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Slider, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Input, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Select, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Video, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)

    protected open fun visit(data: Div.Switch, resolver: ExpressionResolver, path: DivStatePath) =
        defaultVisit(data, resolver, path)
}
