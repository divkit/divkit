package com.yandex.div.internal.core

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.util.mapIndexedNotNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivText

internal data class DivTextImageResult(
    val image: DivText.Image,
    val resolver: ExpressionResolver,
)

internal fun DivText.buildImages(resolver: ExpressionResolver, path: DivStatePath): List<DivTextImageResult>? {
    imageBuilder?.let { return it.build(resolver, path) }
    return images?.map { DivTextImageResult(it, resolver) }
}

internal fun DivText.Ellipsis.buildImages(resolver: ExpressionResolver, path: DivStatePath): List<DivTextImageResult>? {
    imageBuilder?.let { return it.build(resolver, path) }
    return images?.map { DivTextImageResult(it, resolver) }
}

internal fun DivText.ImageBuilder.build(resolver: ExpressionResolver, path: DivStatePath): List<DivTextImageResult> =
    data.evaluate(resolver).mapIndexedNotNull { index, element -> buildItem(element, index, resolver, path) }

private fun DivText.ImageBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
    path: DivStatePath,
): DivTextImageResult? {
    val newResolver = getItemResolver(dataElementName, data, index, resolver, path, "image:") ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return DivTextImageResult(prototype.image.copy(), newResolver)
}
