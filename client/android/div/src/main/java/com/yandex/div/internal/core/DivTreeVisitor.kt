package com.yandex.div.internal.core

import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivPathUtils.getItemIds
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder

internal abstract class DivTreeVisitor<T>(private val returnCondition: ((T) -> Boolean)? = null) {

    protected fun visit(div: Div, parentContext: BindingContext, path: DivStatePath): T {
        val context = parentContext.getChildContext(div, path)
        return when (div) {
            is Div.Text -> visit(div, context, path)
            is Div.Image -> visit(div, context, path)
            is Div.GifImage -> visit(div, context, path)
            is Div.Separator -> visit(div, context, path)
            is Div.Container -> visit(div, context, path)
            is Div.Grid -> visit(div, context, path)
            is Div.Gallery -> visit(div, context, path)
            is Div.Pager -> visit(div, context, path)
            is Div.Tabs -> visit(div, context, path)
            is Div.State -> visit(div, context, path)
            is Div.Custom -> visit(div, context, path)
            is Div.Indicator -> visit(div, context, path)
            is Div.Slider -> visit(div, context, path)
            is Div.Input -> visit(div, context, path)
            is Div.Select -> visit(div, context, path)
            is Div.Video -> visit(div, context, path)
            is Div.Switch -> visit(div, context, path)
        }
    }

    protected abstract fun defaultVisit(data: Div, context: BindingContext, path: DivStatePath): T

    protected open fun defaultVisitCollection(
        data: Div,
        context: BindingContext,
        path: DivStatePath,
        items: List<Div>?,
        itemBuilder: DivCollectionItemBuilder? = null,
        pathOverride: List<DivStatePath>? = null
    ): T {
        val result = defaultVisit(data, context, path)
        if (returnCondition?.invoke(result) == true) return result

        itemBuilder?.let {
            return it.visit(context, path, result)
        }

        val ids = items?.getIds() ?: return result
        items.forEachIndexed { index, div ->
            val childPath = pathOverride?.get(index) ?: path.appendDiv(ids[index])
            val child = visitCollectionChild(div, context, childPath, result)
            if (returnCondition?.invoke(child) == true) return child
        }
        return result
    }

    private fun DivCollectionItemBuilder.visit(
        context: BindingContext,
        path: DivStatePath,
        parent: T,
    ): T {
        val builtItems = build(context.expressionResolver)
        val ids = builtItems.getItemIds()
        builtItems.forEachIndexed { index, item ->
            val childPath = path.appendDiv(ids[index])
            val resolver = context.divView.runtimeStore.resolveRuntimeWith(
                childPath,
                item.div,
                item.expressionResolver,
                context.expressionResolver
            )?.expressionResolver ?: item.expressionResolver
            val childContext = BindingContext(context.divView, resolver)

            val result = visitCollectionChild(item.div, childContext, childPath, parent)
            if (returnCondition?.invoke(result) == true) return result
        }
        return parent
    }

    protected open fun visitCollectionChild(div: Div, context: BindingContext, path: DivStatePath, parent: T) =
        visit(div, context, path)

    protected open fun visit(data: Div.Container, context: BindingContext, path: DivStatePath) =
        defaultVisitCollection(data, context, path, data.value.items, data.value.itemBuilder)

    protected open fun visit(data: Div.Grid, context: BindingContext, path: DivStatePath) =
        defaultVisitCollection(data, context, path, data.value.items)

    protected open fun visit(data: Div.Gallery, context: BindingContext, path: DivStatePath) =
        defaultVisitCollection(data, context, path, data.value.items, data.value.itemBuilder)

    protected open fun visit(data: Div.Pager, context: BindingContext, path: DivStatePath) =
        defaultVisitCollection(data, context, path, data.value.items, data.value.itemBuilder)

    protected open fun visit(data: Div.Tabs, context: BindingContext, path: DivStatePath) =
        defaultVisitCollection(data, context, path, data.value.items.map { it.div })

    protected open fun visit(data: Div.State, context: BindingContext, path: DivStatePath): T {
        val id = data.value.getId()
        val paths = data.value.states.mapNotNull {
            it.div ?: return@mapNotNull null
            path.append(id, it, it.stateId)
        }
        return defaultVisitCollection(data, context, path, data.value.states.mapNotNull { it.div }, null, paths)
    }

    protected open fun visit(data: Div.Custom, context: BindingContext, path: DivStatePath) =
        defaultVisitCollection(data, context, path, data.value.items)

    protected open fun visit(data: Div.Text, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Image, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.GifImage, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Separator, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Indicator, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Slider, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Input, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Select, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Video, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)

    protected open fun visit(data: Div.Switch, context: BindingContext, path: DivStatePath) =
        defaultVisit(data, context, path)
}

internal fun BindingContext.getChildContext(div: Div, path: DivStatePath): BindingContext {
    val runtime = divView.runtimeStore.getOrCreateRuntime(path, div, expressionResolver)
    return getFor(runtime.expressionResolver)
}
