package com.yandex.div.internal.core

import com.yandex.div.core.expression.local.needLocalRuntime
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.resolvePath
import com.yandex.div2.Div
import com.yandex.div2.DivTabs

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
        items: () -> List<Triple<Div, BindingContext, DivStatePath>>
    ): T {
        val result = defaultVisit(data, context, path)
        if (returnCondition?.invoke(result) == true) return result

        items().forEach { (div, context, path) ->
            visit(div, context, path)?.let {
                if (returnCondition?.invoke(it) == true) return it
            }
        }
        return result
    }

    protected open fun visit(data: Div.Container, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.buildItems(context.divView,context.expressionResolver).mapItemWithContext(context, path)
        }
    }

    protected open fun visit(data: Div.Grid, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.nonNullItems.mapDivWithContext(context, path)
        }
    }

    protected open fun visit(data: Div.Gallery, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.buildItems(context.divView, context.expressionResolver).mapItemWithContext(context, path)
        }
    }

    protected open fun visit(data: Div.Pager, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.buildItems(context.divView, context.expressionResolver).mapItemWithContext(context, path)
        }
    }

    protected open fun visit(data: Div.Tabs, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.items.mapIndexed { index: Int, item: DivTabs.Item ->
                Triple(
                    item.div,
                    context,
                    item.div.value().resolvePath(index, path)
                )
            }
        }
    }

    protected open fun visit(data: Div.State, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.states.mapNotNull {
                val div = it.div ?: return@mapNotNull null
                Triple(div, context, path.append(data.value.getId(), it.stateId))
            }
        }
    }

    protected open fun visit(data: Div.Custom, context: BindingContext, path: DivStatePath): T {
        return defaultVisitCollection(data, context, path) {
            data.value.nonNullItems.mapDivWithContext(context, path)
        }
    }

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

    private fun List<DivItemBuilderResult>.mapItemWithContext(
        context: BindingContext,
        parentPath: DivStatePath
    ): List<Triple<Div, BindingContext, DivStatePath>> {
        return mapIndexed { index: Int, item: DivItemBuilderResult ->
            Triple(
                item.div,
                context.getFor(item.expressionResolver),
                item.div.value().resolvePath(index, parentPath)
            )
        }
    }

    private fun List<Div>.mapDivWithContext(
        context: BindingContext,
        parentPath: DivStatePath
    ): List<Triple<Div, BindingContext, DivStatePath>> {
        return mapIndexed { index: Int, div: Div ->
            Triple(div, context, div.value().resolvePath(index, parentPath))
        }
    }
}

internal fun BindingContext.getChildContext(div: Div, path: DivStatePath): BindingContext {
    if (!div.needLocalRuntime) return this
    val resolver = runtimeStore?.getOrCreateRuntime(path.fullPath, div, expressionResolver)?.expressionResolver
        ?: expressionResolver
    return getFor(resolver)
}
