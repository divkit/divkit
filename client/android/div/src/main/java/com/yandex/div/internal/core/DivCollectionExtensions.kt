package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.expression.variables.ConstantsProvider
import com.yandex.div.core.state.DivPathUtils.append
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivPathUtils.getIds
import com.yandex.div.core.state.DivPathUtils.getItemIds
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

@InternalApi
fun DivBlock.Container.buildItems(): List<DivBlock> =
    buildItems(divValue.items, divValue.itemBuilder, expressionResolver, path)

@InternalApi
fun DivBlock.Gallery.buildItems(): List<DivBlock> =
    buildItems(divValue.items, divValue.itemBuilder, expressionResolver, path)

@InternalApi
fun DivBlock.Pager.buildItems(): List<DivBlock> =
    buildItems(divValue.items, divValue.itemBuilder, expressionResolver, path)

@InternalApi
fun DivContainer.buildItems(resolver: ExpressionResolver, path: DivStatePath): List<DivBlock> =
    buildItems(items, itemBuilder, resolver, path)

@InternalApi
fun DivGallery.buildItems(resolver: ExpressionResolver, path: DivStatePath): List<DivBlock> =
    buildItems(items, itemBuilder, resolver, path)

@InternalApi
fun DivPager.buildItems(resolver: ExpressionResolver,  path: DivStatePath): List<DivBlock> =
    buildItems(items, itemBuilder, resolver, path)

private fun buildItems(
    items: List<Div>?,
    itemBuilder: DivCollectionItemBuilder?,
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivBlock> {
    return itemBuilder?.build(resolver, path)
        ?: items?.toBlocks(resolver, path)
        ?: emptyList()
}

internal fun DivCollectionItemBuilder.build(
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivBlock> {
    val items = data.evaluate(resolver).mapIndexedNotNull { i, obj -> buildItem(obj, i, resolver, path) }
    return items.zip(items.getItemIds()) { item, id ->
        val childPath = path.appendDiv(id)
        val childResolver = resolver.asImpl?.runtimeStore
            ?.resolveRuntimeWith(childPath, item.div, item.expressionResolver, resolver)
            ?.expressionResolver ?: item.expressionResolver
        item.div.toBlock(childResolver, childPath)
    }
}

private fun DivCollectionItemBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
    path: DivStatePath,
): DivBlock? {
    val (newResolver, childPath) = getItemResolverAndPath(dataElementName, data, index, resolver, path, "")
        ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return prototype.div.copy(prototype.id?.evaluate(newResolver)).toBlock(newResolver, childPath)
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

val DivContainer.nonNullItems: List<Div> get() = items ?: emptyList()

val DivCustom.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGallery.nonNullItems: List<Div> get() = items ?: emptyList()

val DivGrid.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivGrid.itemsToDivBlocks(resolver: ExpressionResolver, path: DivStatePath) =
    nonNullItems.toBlocks(resolver, path)

internal fun DivBlock.Grid.itemsToDivBlocks() = divValue.nonNullItems.toBlocks(expressionResolver, path)

val DivPager.nonNullItems: List<Div> get() = items ?: emptyList()

internal fun DivTabs.itemsToDivBlocks(resolver: ExpressionResolver, path: DivStatePath) =
    items.toBlocks(resolver, path) { div }

internal fun DivBlock.Tabs.itemsToDivBlocks() = divValue.items.toBlocks(expressionResolver, path) { div }

internal fun DivBlock.State.statesToDivBlocks() = divValue.statesToDivBlocks(expressionResolver, path)

internal fun DivState.statesToDivBlocks(
    resolver: ExpressionResolver,
    path: DivStatePath,
): List<DivBlock> {
    val id = getId()
    val paths = states.map {
        it.div ?: return@map null
        path.append(id, it, it.stateId)
    }
    return states.mapIndexedNotNull { index, state ->
        val div = state.div ?: return@mapIndexedNotNull null
        val statePath = paths[index] ?: return@mapIndexedNotNull null
        val stateResolver = resolver.asImpl?.runtimeStore
            ?.getOrCreateRuntime(statePath.fullPath, div, resolver)
            ?.expressionResolver ?: resolver
        div.toBlock(stateResolver, statePath)
    }
}

internal fun DivBlock.Custom.itemsToDivBlocks() =
    divValue.items?.toBlocks(expressionResolver, path) ?: emptyList()

internal fun List<Div>.toBlocks(resolver: ExpressionResolver, path: DivStatePath) =
    toBlocks(resolver, path) { this }

private fun <T> List<T>.toBlocks(
    resolver: ExpressionResolver,
    path: DivStatePath,
    getDiv: T.() -> Div,
): List<DivBlock> {
    return zip(getIds(getDiv)) { item, id ->
        val childPath = path.appendDiv(id)
        val targetResolver = resolver.asImpl?.runtimeStore
            ?.getOrCreateRuntime(childPath.fullPath, item.getDiv(), resolver)
            ?.expressionResolver ?: resolver
        item.getDiv().toBlock(targetResolver, childPath)
    }
}

internal fun Div.toBlock(
    resolver: ExpressionResolver,
    path: DivStatePath,
) = DivBlock.create(this, resolver, path)
