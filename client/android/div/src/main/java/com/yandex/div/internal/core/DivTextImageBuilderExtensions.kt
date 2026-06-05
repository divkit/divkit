package com.yandex.div.internal.core

import com.yandex.div.internal.util.forEach
import com.yandex.div.internal.util.mapIndexedNotNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivText

internal data class DivTextImageResult(
    val image: DivText.Image,
    val resolver: ExpressionResolver,
)

internal fun DivText.buildImages(resolver: ExpressionResolver): List<DivTextImageResult>? {
    imageBuilder?.let { return it.build(resolver) }
    return images?.map { DivTextImageResult(it, resolver) }
}

internal fun DivText.Ellipsis.buildImages(resolver: ExpressionResolver): List<DivTextImageResult>? {
    imageBuilder?.let { return it.build(resolver) }
    return images?.map { DivTextImageResult(it, resolver) }
}

internal fun DivText.ImageBuilder.build(resolver: ExpressionResolver): List<DivTextImageResult> =
    data.evaluate(resolver).mapIndexedNotNull { index, element -> buildItem(element, index, resolver) }

private fun DivText.ImageBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
): DivTextImageResult? {
    val newResolver = getItemResolver(data, index, resolver) ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return DivTextImageResult(prototype.image.copy(), newResolver)
}

internal fun DivText.ImageBuilder.getItemResolver(resolver: ExpressionResolver): ExpressionResolver {
    data.evaluate(resolver).forEach<Any> { index, element ->
        getItemResolver(element, index, resolver)?.let { return it }
    }
    return resolver
}

private fun DivText.ImageBuilder.getItemResolver(
    dataElement: Any,
    index: Int,
    resolver: ExpressionResolver,
) = getItemBuilderResolver(dataElementName, dataElement, index, resolver, "image:")
