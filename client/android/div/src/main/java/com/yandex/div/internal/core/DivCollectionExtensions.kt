package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.variables.VariableSource
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.forEach
import com.yandex.div.internal.util.mapIndexedNotNull
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

private const val INDEX_VARIABLE_NAME = "index"

@InternalApi
fun DivContainer.buildItems(resolver: ExpressionResolver): List<DivItemBuilderResult> =
    buildItems(items, itemBuilder, resolver)

@InternalApi
fun DivGallery.buildItems(resolver: ExpressionResolver): List<DivItemBuilderResult> =
    buildItems(items, itemBuilder, resolver)

@InternalApi
fun DivPager.buildItems(resolver: ExpressionResolver): List<DivItemBuilderResult> =
    buildItems(items, itemBuilder, resolver)

private fun buildItems(items: List<Div>?, itemBuilder: DivCollectionItemBuilder?, resolver: ExpressionResolver) =
    items?.toDivItemBuilderResult(resolver) ?: itemBuilder?.build(resolver) ?: emptyList()

internal fun DivCollectionItemBuilder.build(resolver: ExpressionResolver): List<DivItemBuilderResult> =
    data.evaluate(resolver).mapIndexedNotNull { i, obj -> buildItem(obj, i, resolver) }

private fun DivCollectionItemBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
): DivItemBuilderResult? {
    val newResolver = getItemResolver(data, index, resolver) ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return prototype.div.copy(prototype.id?.evaluate(newResolver)).toItemBuilderResult(newResolver)
}

internal fun DivCollectionItemBuilder.getItemResolver(resolver: ExpressionResolver): ExpressionResolver {
    data.evaluate(resolver).forEach<Any> { i, obj ->
        getItemResolver(obj, i, resolver)?.let { return it }
    }
    return resolver
}

private fun DivCollectionItemBuilder.getItemResolver(
    dataElement: Any,
    index: Int,
    resolver: ExpressionResolver,
): ExpressionResolver? {
    val resolverImpl = resolver as? ExpressionResolverImpl ?: return resolver
    val validElement = resolverImpl.validateItemBuilderDataElement(dataElement, index) ?: return null
    val localVariableSource = VariableSource(
        mapOf(
            dataElementName to Variable.DictVariable(dataElementName, validElement),
            INDEX_VARIABLE_NAME to Variable.IntegerVariable(INDEX_VARIABLE_NAME, index.toLong())
        ),
        {},
        mutableListOf()
    )
    return resolverImpl + localVariableSource
}

private fun Div.copy(id: String? = value().id): Div {
    return when (this) {
        is Div.Image -> Div.Image(value.copy(id = id))
        is Div.GifImage -> Div.GifImage(value.copy(id = id))
        is Div.Text -> Div.Text(value.copy(id = id))
        is Div.Separator -> Div.Separator(value.copy(id = id))
        is Div.Container -> Div.Container(value.copy(
            id = id,
            items = value.items?.map { it.copy() },
        ))
        is Div.Grid -> Div.Grid(value.copy(
            id = id,
            items = value.items?.map { it.copy() }
        ))
        is Div.Gallery -> Div.Gallery(value.copy(
            id = id,
            items = value.items?.map { it.copy() }
        ))
        is Div.Pager -> Div.Pager(value.copy(
            id = id,
            items = value.items?.map { it.copy() }
        ))
        is Div.Tabs -> Div.Tabs(value.copy(
            id = id,
            items = value.items.map { it.copy(div = it.div.copy()) }
        ))
        is Div.State -> Div.State(value.copy(
            id = id,
            divId = id,
            states = value.states.map { it.copy(div = it.div?.copy()) }
        ))
        is Div.Custom -> Div.Custom(value.copy(id = id))
        is Div.Indicator -> Div.Indicator(value.copy(id = id))
        is Div.Slider -> Div.Slider(value.copy(id = id))
        is Div.Input -> Div.Input(value.copy(id = id))
        is Div.Select -> Div.Select(value.copy(id = id))
        is Div.Video -> Div.Video(value.copy(id = id))
    }
}

val DivContainer.nonNullItems: List<Div> get() = items ?: emptyList()

val DivCustom.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGallery.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGrid.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivGrid.itemsToDivItemBuilderResult(resolver: ExpressionResolver) =
    nonNullItems.toDivItemBuilderResult(resolver)

val DivPager.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivTabs.itemsToDivItemBuilderResult(resolver: ExpressionResolver) =
    items.map { it.div.toItemBuilderResult(resolver) }

internal fun DivState.statesToDivItemBuilderResult(resolver: ExpressionResolver) =
    states.mapNotNull { state -> state.div?.toItemBuilderResult(resolver) }

internal fun List<Div>.toDivItemBuilderResult(resolver: ExpressionResolver) = map { it.toItemBuilderResult(resolver) }

internal fun Div.toItemBuilderResult(resolver: ExpressionResolver) = DivItemBuilderResult(this, resolver)
