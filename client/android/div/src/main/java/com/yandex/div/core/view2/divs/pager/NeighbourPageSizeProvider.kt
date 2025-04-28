package com.yandex.div.core.view2.divs.pager

import android.util.DisplayMetrics
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivNeighbourPageSize
import com.yandex.div2.DivPager.ItemAlignment

internal class NeighbourPageSizeProvider(
    mode: DivNeighbourPageSize,
    resolver: ExpressionResolver,
    metrics: DisplayMetrics,
    parentSize: Int,
    itemSpacing: Float,
    paddings: DivPagerPaddingsHolder,
    alignment: ItemAlignment,
) : DivPagerPageSizeProvider(parentSize, paddings, alignment), FixedPageSizeProvider {

    private val neighbourPageWidth = mode.neighbourPageWidth.toPxF(metrics, resolver)

    override val neighbourSize = neighbourPageWidth + itemSpacing

    override val itemSize = when (alignment) {
        ItemAlignment.START -> parentSize - paddings.start - neighbourSize
        ItemAlignment.CENTER -> parentSize - neighbourSize * 2
        ItemAlignment.END -> parentSize - paddings.end - neighbourSize
    }

    override val hasOffScreenPages = neighbourPageWidth > 0

    override fun getItemSize(position: Int) = itemSize
}
