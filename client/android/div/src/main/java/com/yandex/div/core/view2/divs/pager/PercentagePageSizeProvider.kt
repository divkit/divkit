package com.yandex.div.core.view2.divs.pager

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivPageSize

internal class PercentagePageSizeProvider(
    mode: DivPageSize,
    resolver: ExpressionResolver,
    parentSize: Int,
) : DivPagerPageSizeProvider {

    private val pageWidthPercentage = mode.pageWidth.value.evaluate(resolver)

    override val itemSize = (parentSize * pageWidthPercentage / 100).toFloat()

    override val neighbourSize = (parentSize - itemSize) / 2

    override val hasOffScreenPages = pageWidthPercentage < 100
}
