package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.expression.variables.ConstantsProvider
import com.yandex.div.core.state.DivPathUtils.append
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivStatePath
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

internal const val INDEX_VARIABLE_NAME = "index"

@InternalApi
fun DivContainer.buildItems(resolver: ExpressionResolver, path: DivStatePath): List<DivItemBuilderResult> =
    buildItems(items, itemBuilder, resolver, path)

@InternalApi
fun DivGallery.buildItems(resolver: ExpressionResolver, path: DivStatePath): List<DivItemBuilderResult> =
    buildItems(items, itemBuilder, resolver, path)

@InternalApi
fun DivPager.buildItems(resolver: ExpressionResolver,  path: DivStatePath): List<DivItemBuilderResult> =
    buildItems(items, itemBuilder, resolver, path)

private fun buildItems(
    items: List<Div>?,
    itemBuilder: DivCollectionItemBuilder?,
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivItemBuilderResult> {
    return itemBuilder?.build(resolver, path)
        ?: items?.toDivItemBuilderResult(resolver, path)
        ?: emptyList()
}

internal fun DivCollectionItemBuilder.build(
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivItemBuilderResult> =
    data.evaluate(resolver).mapIndexedNotNull { i, obj -> buildItem(obj, i, resolver, path) }

private fun DivCollectionItemBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
    path: DivStatePath,
): DivItemBuilderResult? {
    val (newResolver, childPath) = getItemResolverAndPath(dataElementName, data, index, resolver, path, "")
        ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return prototype.div.copy(prototype.id?.evaluate(newResolver)).toItemBuilderResult(newResolver, childPath)
}

internal fun getItemResolver(
    dataElementName: String,
    dataElement: Any,
    index: Int,
    resolver: ExpressionResolver,
    path: DivStatePath,
    pathPrefix: String,
): ExpressionResolver? = getItemResolverAndPath(dataElementName, dataElement, index, resolver, path, pathPrefix)?.first

private fun getItemResolverAndPath(
    dataElementName: String,
    dataElement: Any,
    index: Int,
    resolver: ExpressionResolver,
    path: DivStatePath,
    pathPrefix: String,
): Pair<ExpressionResolver, DivStatePath>? {
    val resolverImpl = resolver.asImpl ?: return null
    val validElement = resolverImpl.validateItemBuilderDataElement(dataElement, index) ?: return null
    val pathSegment = "$pathPrefix$dataElement:$index"
    val childPath = path.appendDiv(pathSegment)
    return resolverImpl.runtimeStore.getOrPutItemBuilderResolver(childPath.fullPath) {
        val localDataProvider = ConstantsProvider(mapOf(
            dataElementName to validElement,
            INDEX_VARIABLE_NAME to index.toLong()
        ))
        resolverImpl.withConstants(pathSegment, localDataProvider)
    } to childPath
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
        is Div.Switch -> Div.Switch(value.copy(id = id))
    }
}

val DivContainer.nonNullItems: List<Div> get() = items ?: emptyList()

val DivCustom.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGallery.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGrid.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivGrid.itemsToDivItemBuilderResult(resolver: ExpressionResolver, path: DivStatePath) =
    nonNullItems.toDivItemBuilderResult(resolver, path)

val DivPager.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivTabs.itemsToDivItemBuilderResult(resolver: ExpressionResolver, path: DivStatePath) =
    items.map { it.div.toItemBuilderResult(resolver, path) }

internal fun DivState.statesToDivItemBuilderResult(
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivItemBuilderResult> {
    val id = getId()
    val paths = states.mapNotNull {
        it.div ?: return@mapNotNull null
        path.append(id, it, it.stateId)
    }
    return states.mapIndexedNotNull { index, state -> state.div?.toItemBuilderResult(resolver, paths[index]) }
}

internal fun List<Div>.toDivItemBuilderResult(
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivItemBuilderResult> {
    val ids = getIds()
    return mapIndexed { index, div ->
        val childPath = path.appendDiv(ids[index])
        val targetResolver = resolver.asImpl?.runtimeStore
            ?.getOrCreateRuntime(childPath.fullPath, div, resolver)?.expressionResolver
            ?: resolver
        div.toItemBuilderResult(targetResolver, childPath)
    }
}

internal fun Div.toItemBuilderResult(
    resolver: ExpressionResolver,
    path: DivStatePath,
) = DivItemBuilderResult(this, resolver, path)
