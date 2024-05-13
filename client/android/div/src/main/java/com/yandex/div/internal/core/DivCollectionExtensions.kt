package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.variables.VariableSource
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.mapNotNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import org.json.JSONObject

@InternalApi
fun DivContainer.buildItems(resolver: ExpressionResolver): List<DivItemBuilderResult> =
    items?.map { DivItemBuilderResult(it, resolver) } ?: itemBuilder?.build(resolver) ?: emptyList()

private fun DivCollectionItemBuilder.build(resolver: ExpressionResolver): List<DivItemBuilderResult> {
    return data.evaluate(resolver).mapNotNull {
        (it as? JSONObject)?.let { json -> buildItem(json, resolver) }
    }
}

private fun DivCollectionItemBuilder.buildItem(
    data: JSONObject,
    resolver: ExpressionResolver,
): DivItemBuilderResult? {
    val localVariableSource = VariableSource(
        mapOf(dataElementName to Variable.DictVariable(dataElementName, data)),
        {},
        mutableListOf()
    )
    val newResolver = (resolver as? ExpressionResolverImpl)?.plus(localVariableSource) ?: resolver
    val prototype = prototypes.find { it.selector.evaluate(newResolver) }?.div ?: return null
    return DivItemBuilderResult(prototype.copy(), newResolver)
}

private fun Div.copy(): Div {
    return when (this) {
        is Div.Image -> Div.Image(value.copy())
        is Div.GifImage -> Div.GifImage(value.copy())
        is Div.Text -> Div.Text(value.copy())
        is Div.Separator -> Div.Separator(value.copy())
        is Div.Container -> Div.Container(value.copy(
            itemBuilder = value.itemBuilder?.copy(),
            items = value.items?.map { it.copy() },
        ))
        is Div.Grid -> Div.Grid(value.copy(items = value.items?.map { it.copy() }))
        is Div.Gallery -> Div.Gallery(value.copy(items = value.items?.map { it.copy() }))
        is Div.Pager -> Div.Pager(value.copy(items = value.items?.map { it.copy() }))
        is Div.Tabs -> Div.Tabs(value.copy(items = value.items.map { it.copy(div = it.div.copy()) }))
        is Div.State -> Div.State(value.copy(states = value.states.map { it.copy(div = it.div?.copy()) }))
        is Div.Custom -> Div.Custom(value.copy())
        is Div.Indicator -> Div.Indicator(value.copy())
        is Div.Slider -> Div.Slider(value.copy())
        is Div.Input -> Div.Input(value.copy())
        is Div.Select -> Div.Select(value.copy())
        is Div.Video -> Div.Video(value.copy())
    }
}

val DivContainer.nonNullItems: List<Div> get() = items ?: emptyList()

val DivCustom.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGallery.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivGallery.itemsToDivItemBuilderResult(resolver: ExpressionResolver) =
    nonNullItems.toDivItemBuilderResult(resolver)

val DivGrid.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivGrid.itemsToDivItemBuilderResult(resolver: ExpressionResolver) =
    nonNullItems.toDivItemBuilderResult(resolver)

val DivPager.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivPager.itemsToDivItemBuilderResult(resolver: ExpressionResolver) =
    nonNullItems.toDivItemBuilderResult(resolver)

internal fun DivTabs.itemsToDivItemBuilderResult(resolver: ExpressionResolver) =
    items.map { DivItemBuilderResult(it.div, resolver) }

internal fun DivState.statesToDivItemBuilderResult(resolver: ExpressionResolver) =
    states.mapNotNull { state -> state.div?.let { DivItemBuilderResult(it, resolver) } }

private fun List<Div>.toDivItemBuilderResult(resolver: ExpressionResolver) = map { DivItemBuilderResult(it, resolver) }
