package com.yandex.div.internal.core

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.util.mapIndexedNotNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivText

internal data class DivTextRangeResult(
    val range: DivText.Range,
    val resolver: ExpressionResolver,
)

internal fun DivText.buildRanges(resolver: ExpressionResolver, path: DivStatePath): List<DivTextRangeResult>? {
    rangeBuilder?.let { return it.build(resolver, path) }
    return ranges?.map { DivTextRangeResult(it, resolver) }
}

internal fun DivText.Ellipsis.buildRanges(resolver: ExpressionResolver, path: DivStatePath): List<DivTextRangeResult>? {
    rangeBuilder?.let { return it.build(resolver, path) }
    return ranges?.map { DivTextRangeResult(it, resolver) }
}

internal fun DivText.RangeBuilder.build(resolver: ExpressionResolver, path: DivStatePath): List<DivTextRangeResult> =
    data.evaluate(resolver).mapIndexedNotNull { index, element -> buildItem(element, index, resolver, path) }

private fun DivText.RangeBuilder.buildItem(
    data: Any,
    index: Int,
    resolver: ExpressionResolver,
    path: DivStatePath,
): DivTextRangeResult? {
    val newResolver = getItemResolver(dataElementName, data, index, resolver, path, "range:") ?: return null
    val prototype = prototypes.find { it.selector.evaluate(newResolver) } ?: return null
    return DivTextRangeResult(prototype.range.copy(), newResolver)
}
