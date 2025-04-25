package com.yandex.div.core.view2.divs.pager

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager.ItemAlignment

internal class PercentagePageSizeProvider(
    mode: DivPageSize,
    resolver: ExpressionResolver,
    parentSize: Int,
    paddings: DivPagerPaddingsHolder,
    alignment: ItemAlignment,
) : DivPagerPageSizeProvider(parentSize, paddings, alignment), FixedPageSizeProvider {

    private val pageWidthPercentage = mode.pageWidth.value.evaluate(resolver)

    override val itemSize = (parentSize * pageWidthPercentage / 100).toFloat()

    override val neighbourSize = when (alignment) {
        ItemAlignment.START -> parentSize - paddings.start - itemSize
        ItemAlignment.CENTER -> (parentSize - itemSize) / 2
        ItemAlignment.END -> parentSize - paddings.end - itemSize
    }

    override val hasOffScreenPages = pageWidthPercentage < 100

    override fun getItemSize(position: Int) = itemSize
}
