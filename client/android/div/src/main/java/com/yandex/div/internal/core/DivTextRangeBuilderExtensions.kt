package com.yandex.div.internal.core

import com.yandex.div.internal.util.forEach
import com.yandex.div.internal.util.mapIndexedNotNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivText

internal data class DivTextRangeResult(
    val range: DivText.Range,
    val resolver: ExpressionResolver,
)

internal fun DivText.buildRanges(resolver: ExpressionResolver): List<DivTextRangeResult>? {
    rangeBuilder?.let { return it.build(resolver) }
    return ranges?.map { DivTextRangeResult(it, resolver) }
}

internal fun DivText.Ellipsis.buildRanges(resolver: ExpressionResolver): List<DivTextRangeResult>? {
    rangeBuilder?.let { return it.build(resolver) }
    return ranges?.map { DivTextRangeResult(it, resolver) }
}

internal fun DivText.RangeBuilder.build(resolver: ExpressionResolver): List<DivTextRangeResult> =
    data.evaluate(resolver).mapIndexedNotNull { index, element -> buildItem(element, index, resolver) }

private fun DivText.RangeBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
): DivTextRangeResult? {
    val newResolver = getItemResolver(data, index, resolver) ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return DivTextRangeResult(prototype.range.copy(), newResolver)
}

internal fun DivText.RangeBuilder.getItemResolver(resolver: ExpressionResolver): ExpressionResolver {
    data.evaluate(resolver).forEach<Any> { index, element ->
        getItemResolver(element, index, resolver)?.let { return it }
    }
    return resolver
}

private fun DivText.RangeBuilder.getItemResolver(
    dataElement: Any,
    index: Int,
    resolver: ExpressionResolver,
) = getItemBuilderResolver(dataElementName, dataElement, index, resolver, "range:")
