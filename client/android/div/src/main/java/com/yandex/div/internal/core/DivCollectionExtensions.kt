package com.yandex.div.internal.core

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.variables.VariableSource
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager
import org.json.JSONObject

fun DivContainer.buildItems(resolver: ExpressionResolver): List<DivItemBuilderResult> =
    items?.map { DivItemBuilderResult(it, resolver) } ?: itemBuilder?.build(resolver) ?: emptyList()

private fun DivCollectionItemBuilder.build(
    resolver: ExpressionResolver
): List<DivItemBuilderResult> {
    result?.let { return it }

    val result = mutableListOf<DivItemBuilderResult>()
    data.evaluate(resolver).forEach { _, json: JSONObject ->
        buildItem(json, resolver)?.let { result.add(it) }
    }
    return result
}

internal fun DivCollectionItemBuilder.buildItem(
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

val DivGallery.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGrid.nonNullItems: List<Div> get() = items ?: emptyList()

val DivPager.nonNullItems: List<Div> get() = items ?: emptyList()
